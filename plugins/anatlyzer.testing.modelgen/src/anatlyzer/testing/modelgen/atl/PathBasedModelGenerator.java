package anatlyzer.testing.modelgen.atl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.annotation.NonNull;

import analyser.atl.problems.IDetectedProblem;
import anatlyzer.atl.analyser.IAnalyserResult;
import anatlyzer.atl.analyser.generators.CSPGenerator;
import anatlyzer.atl.analyser.generators.CSPModel;
import anatlyzer.atl.analyser.generators.ErrorSlice;
import anatlyzer.atl.analyser.generators.OclSlice;
import anatlyzer.atl.analyser.generators.RetypingStrategy;
import anatlyzer.atl.analyser.generators.TransformationSlice;
import anatlyzer.atl.errors.ProblemStatus;
import anatlyzer.atl.errors.atl_error.LocalProblem;
import anatlyzer.atl.graph.AbstractDependencyNode;
import anatlyzer.atl.graph.FeatureNotSupported;
import anatlyzer.atl.graph.GenericErrorNode;
import anatlyzer.atl.graph.GraphNode;
import anatlyzer.atl.graph.IPathVisitor;
import anatlyzer.atl.graph.PathGenerator;
import anatlyzer.atl.graph.ProblemNode;
import anatlyzer.atl.graph.ProblemPath;
import anatlyzer.atl.util.ATLSerializer;
import anatlyzer.atl.util.ATLUtils;
import anatlyzer.atl.util.AnalyserUtils;
import anatlyzer.atl.witness.ConstraintSatisfactionChecker;
import anatlyzer.atl.witness.IWitnessFinder;
import anatlyzer.atl.witness.UseWitnessFinder;
import anatlyzer.atl.witness.IWitnessFinder.WitnessGenerationMode;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.Unit;
import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.PropertyCallExp;
import anatlyzer.atlext.OCL.VariableDeclaration;
import anatlyzer.atlext.OCL.VariableExp;
import anatlyzer.atlext.processing.AbstractVisitor;
import anatlyzer.testing.common.IProgressMonitor;
import anatlyzer.testing.common.Metamodel;
import anatlyzer.testing.modelgen.AbstractModelGenerator;
import anatlyzer.testing.modelgen.IGeneratedModelReference;
import anatlyzer.testing.modelgen.IModelGenerator;
import anatlyzer.testing.modelgen.IStorageStrategy;

/**
 * This model generators generate models which try to cover all
 * the possible execution paths of an ATL transformation.
 * 
 * @author jesus
 */
public class PathBasedModelGenerator extends AbstractModelGenerator implements IModelGenerator {

	@NonNull 
	private IAnalyserResult trafo;

	public PathBasedModelGenerator(@NonNull IAnalyserResult trafo, @NonNull IStorageStrategy strategy, @NonNull IWitnessFinder finder) {
		super(strategy, finder);
		this.trafo = trafo;
	}
	
	public PathBasedModelGenerator withStorageStrategy(@NonNull IStorageStrategy strategy) {
		this.storageStrategy = strategy;
		return this;
	}
	
	public List<IGeneratedModelReference> generateModels(@NonNull LocatedElement element, @NonNull IProgressMonitor monitor) {
		List<IGeneratedModelReference> generated = new ArrayList<IGeneratedModelReference>(); 
		
		List<? extends Metamodel> metamodels = getMetamodels();		

		PathGenerator generator = new PathGenerator();
		OclExpression path = PathComputationVisitor.getPathCondition(generator, element);
		generateModels(path, metamodels, generated, monitor);
		
		return generated;
	}
	
	@Override
	public List<IGeneratedModelReference> generateModels(@NonNull IProgressMonitor monitor) {
		List<IGeneratedModelReference> generated = new ArrayList<IGeneratedModelReference>(); 
		
		Unit root = trafo.getATLModel().getRoot();
		
		List<OclExpression> paths = new ArrayList<OclExpression>();
		PathComputationVisitor visitor = new PathComputationVisitor(paths);
		visitor.startVisiting(root);

		List<? extends Metamodel> metamodels = getMetamodels();
		
		monitor.beginWork("Model generation", paths.size());
		
		for(OclExpression path : paths) {			
			if (monitor.isCancelled())
				break;
			
			generateModels(path, metamodels, generated, monitor);
			
			if ( limit != -1 && generated.size() > limit ) {
				break;
			}
		}

		return generated;
	}

	private List<Metamodel> getMetamodels() {
		return ATLUtils.getModelInfo(trafo.getATLModel()).stream()
			.filter(i -> i.isInput())
			.map(i -> trafo.getNamespaces().getNamespace(i.getMetamodelName()))
			.map(n -> new Metamodel(n.getResource()))
			.collect(Collectors.toList());
	}
	
	private void generateModels(@NonNull OclExpression path, List<? extends Metamodel> metamodels, List<IGeneratedModelReference> generated, @NonNull IProgressMonitor monitor) {
		wf.setWitnessGenerationModel(WitnessGenerationMode.MANDATORY_FULL_METAMODEL);
		// wf.setScopeCalculator(new GenStrategyScope(propertiesUse));
		
		if ( wf instanceof UseWitnessFinder ) {
			((UseWitnessFinder) wf).withRetyingStrategy(RetypingStrategy.NULL);
			((UseWitnessFinder) wf).setPreferDeclaredTypes(true);
		}
		
		ConstraintSatisfactionChecker checker = ConstraintSatisfactionChecker.
				withExpr(path).
				withFinder(wf);

		for(Map.Entry<String, Resource> mm : trafo.getNamespaces().getLogicalNamesToMetamodels().entrySet()) {
			checker.configureMetamodel(mm.getKey(), mm.getValue());
		}
		
		checker.check();

		ProblemStatus result = checker.getFinderResult();
		
		Metamodel metamodel;
		if ( metamodels.size() == 1 ) {
			metamodel = metamodels.get(0);
		} else {
			// Maybe create a compound metamodel?
			throw new UnsupportedOperationException("Multiple meta-models not supported yet");
		}
		
		if ( AnalyserUtils.isConfirmed(result) ) {
			IGeneratedModelReference ref = storageStrategy.save(wf.getFoundWitnessModel(), metamodel);
			generated.add(ref);
		} else if ( AnalyserUtils.isDiscarded(result)) {
			System.out.println("[NO_MODEL_FOUND]: " + result );
		} else {
			System.out.println("Cannot generate model: " + result );
		}	
		
		if ( monitor != null )
			monitor.workDone("Processed model with result: " + result, 1);	
	}

	private static class PathComputationVisitor extends AbstractVisitor {
		private @NonNull List<OclExpression> paths;
		private @NonNull PathGenerator generator = new PathGenerator();
		// private @NonNull ProblemGraph graph = new ProblemGraph();
		
		public PathComputationVisitor(@NonNull List<OclExpression> paths) {
			this.paths = paths;
		}
		
		@Override
		public void inNavigationOrAttributeCallExp(NavigationOrAttributeCallExp self) {
			addPathForSource(self);
			super.inNavigationOrAttributeCallExp(self);
		}

		//public ProblemGraph getGraph() {
		//	return graph;
		//}
		
		private void addPathForSource(OclExpression expression) {
			// This configuration is not supported by PathGenerator
			if ( expression instanceof VariableExp && ((VariableExp) expression).getReferredVariable().getVarName().equals("thisModule"))
				return;
			try {
				OclExpression pathCondition = getPathCondition(generator, expression);
				if ( pathCondition == null ) {
					System.out.println("Dead code");
					return;
				}
				paths.add(pathCondition);
			} catch ( FeatureNotSupported e ) {
				// TODO: Record this somehow, for instance, it happens in Bibtex2Docbook, because iterate not supported for inlining
				System.out.println("Can't generate path for " + expression);
				e.printStackTrace();
			}
		}

		public static OclExpression getPathCondition(PathGenerator generator, LocatedElement element) {
			ProblemPath path = generator.generatePath(element, PathGenerationNode::new);			
			OclExpression pathCondition = CSPGenerator.generateCSPCondition(path, false);
			return pathCondition;
		}
	}

	public static class PathGenerationNode extends GenericErrorNode {

		public PathGenerationNode(LocalProblem p) {
			super(p);
		}

		@Override
		public OclExpression genCSP(CSPModel model, GraphNode previous) {
			BooleanExp b = OCLFactory.eINSTANCE.createBooleanExp();
			b.setBooleanSymbol(true);
			return b;
//			OclExpression access = model.gen(((PropertyCallExp) expr).getSource());
//			OperationCallExp checkOclIsUndefined = OCLFactory.eINSTANCE.createOperationCallExp();
//			checkOclIsUndefined.setOperationName("oclIsUndefined");
//			checkOclIsUndefined.setSource(access);
//			
//			return checkOclIsUndefined;
		}
	}
}
