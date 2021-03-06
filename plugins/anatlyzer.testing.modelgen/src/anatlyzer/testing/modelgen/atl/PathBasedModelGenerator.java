package anatlyzer.testing.modelgen.atl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.annotation.NonNull;

import analyser.atl.problems.IDetectedProblem;
import anatlyzer.atl.analyser.Analyser;
import anatlyzer.atl.analyser.IAnalyserResult;
import anatlyzer.atl.analyser.generators.CSPGenerator;
import anatlyzer.atl.analyser.generators.CSPModel;
import anatlyzer.atl.analyser.generators.ErrorSlice;
import anatlyzer.atl.analyser.generators.OclSlice;
import anatlyzer.atl.analyser.generators.RetypingStrategy;
import anatlyzer.atl.analyser.generators.TransformationSlice;
import anatlyzer.atl.errors.ProblemStatus;
import anatlyzer.atl.errors.atl_error.LocalProblem;
import anatlyzer.atl.graph.AbstractBindingAssignmentNode;
import anatlyzer.atl.graph.AbstractDependencyNode;
import anatlyzer.atl.graph.FeatureNotSupported;
import anatlyzer.atl.graph.GenericErrorNode;
import anatlyzer.atl.graph.GraphNode;
import anatlyzer.atl.graph.IPathVisitor;
import anatlyzer.atl.graph.PathGenerator;
import anatlyzer.atl.graph.ProblemNode;
import anatlyzer.atl.graph.ProblemPath;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.model.TypeUtils;
import anatlyzer.atl.types.Metaclass;
import anatlyzer.atl.util.ASTUtils;
import anatlyzer.atl.util.ATLCopier;
import anatlyzer.atl.util.ATLSerializer;
import anatlyzer.atl.util.ATLUtils;
import anatlyzer.atl.util.AnalyserUtils;
import anatlyzer.atl.witness.ConstraintSatisfactionChecker;
import anatlyzer.atl.witness.IWitnessFinder;
import anatlyzer.atl.witness.UseWitnessFinder;
import anatlyzer.atl.witness.IWitnessFinder.WitnessGenerationMode;
import anatlyzer.atlext.ATL.Binding;
import anatlyzer.atlext.ATL.ContextHelper;
import anatlyzer.atlext.ATL.Helper;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.Rule;
import anatlyzer.atlext.ATL.RuleResolutionInfo;
import anatlyzer.atlext.ATL.RuleWithPattern;
import anatlyzer.atlext.ATL.Unit;
import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.CollectionOperationCallExp;
import anatlyzer.atlext.OCL.IntegerExp;
import anatlyzer.atlext.OCL.Iterator;
import anatlyzer.atlext.OCL.IteratorExp;
import anatlyzer.atlext.OCL.NavigationOrAttributeCallExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.atlext.OCL.OclModelElement;
import anatlyzer.atlext.OCL.OperationCallExp;
import anatlyzer.atlext.OCL.OperatorCallExp;
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

	public static enum Mode {
		ATL_MATCHED_RULE,
		PATH_PLUS_METAMODEL
	}
	
	@NonNull 
	private IAnalyserResult trafo;
	@NonNull 
	private Mode mode = Mode.ATL_MATCHED_RULE;

	public PathBasedModelGenerator(@NonNull IAnalyserResult trafo, @NonNull IStorageStrategy strategy, @NonNull IWitnessFinder finder) {
		super(strategy, finder);
		this.trafo = trafo;
	}
	
	public PathBasedModelGenerator withMode(Mode mode) {
		this.mode = mode;
		return this;
	}
	
	public PathBasedModelGenerator withStorageStrategy(@NonNull IStorageStrategy strategy) {
		this.storageStrategy = strategy;
		return this;
	}
	
	/**
	 * Generate path-based models for a specific element of the transformation
	 */
	public List<IGeneratedModelReference> generateModels(@NonNull LocatedElement element, @NonNull IProgressMonitor monitor) {
		List<IGeneratedModelReference> generated = new ArrayList<IGeneratedModelReference>(); 
		
		List<? extends Metamodel> metamodels = getMetamodels();		

		PathGenerator generator = new PathGenerator();
		OclExpression path = PathComputationVisitor.getPathCondition(generator, element);
		generateModels(path, metamodels, generated, monitor);
		
		return generated;
	}
	

	/**
	 * Generate path-based models for all possible paths of the transformation
	 */
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
		
//		if ( wf instanceof UseWitnessFinder ) {
//			((UseWitnessFinder) wf).withRetyingStrategy(RetypingStrategy.NULL);
//			((UseWitnessFinder) wf).setPreferDeclaredTypes(true);
//		}
		
		Set<Helper> pathHelpers = new HashSet<>();
		addRequiredHelpers(path, pathHelpers);
						
		List<ModelConstraint> constraints;
		if (mode == Mode.PATH_PLUS_METAMODEL) {
			constraints = getModelVariations_MetamodelBased(path, metamodels);
		} else {
			constraints = Collections.singletonList(new ModelConstraint(path, Collections.emptyList()));
		}
		
		for (ModelConstraint constraint : constraints) {
			Set<Helper> helpers = new HashSet<>(pathHelpers);
			for(OclExpression exp : constraint.extraConstraints) {
				addRequiredHelpers(exp, helpers);				
			}
			
			Set<Helper> copiedHelpers = new HashSet<>();
			for (Helper helper : helpers) {
				copiedHelpers.add((Helper) ATLCopier.copySingleElement(helper));
			}
			
			
			List<OclExpression> allExpressions = new ArrayList<>();
			allExpressions.add(constraint.path);
			System.out.println(ATLSerializer.serialize(constraint.path));
			
			allExpressions.addAll(constraint.extraConstraints);
			
			ConstraintSatisfactionChecker checker = ConstraintSatisfactionChecker.
					withExpr(allExpressions).
					withRequiredHelpers(copiedHelpers).					
					withFinder(wf).
					withGlobal("thisModule");

			for(Map.Entry<String, Resource> mm : trafo.getNamespaces().getLogicalNamesToMetamodels().entrySet()) {
				checker.configureMetamodel(mm.getKey(), mm.getValue());
			}
			
			ProblemStatus result = null;
			try {
				checker.check();
			} catch ( Exception e ) {
				result = ProblemStatus.IMPL_INTERNAL_ERROR;
				e.printStackTrace();
				// TODO: This should not happen but...
			}

			result = checker.getFinderResult();
			
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
		
	}

	/**
	 * Represents the generation of a model given a constraint.
	 */
	private static class ModelConstraint {
		
		private OclExpression path;
		private List<? extends OclExpression> extraConstraints;

		public ModelConstraint(OclExpression path, List<? extends OclExpression> extraConstraints) {
			this.path = path;
			this.extraConstraints = extraConstraints;
		}
	
	}

	private List<ModelConstraint> getModelVariations_MetamodelBased(@NonNull OclExpression path, List<? extends Metamodel> metamodels) {
		// Simple footprint computation
		List<Metaclass> usedMetaclasses = getUsedMetaclasses(path);
		List<Binding> referencingBindings = getReferencingBindings(usedMetaclasses);
		
		Set<EClass> alreadyHandled = new HashSet<>();
		List<OclExpression> expressions = new ArrayList<OclExpression>();
		
		for (Binding binding : referencingBindings) {
			Rule rule = ATLUtils.getRule(binding);
			if (rule instanceof MatchedRule) {
				Metaclass m = ATLUtils.getInPatternType((MatchedRule) rule);
				EClass c = m.getKlass();
				if (! alreadyHandled.add(c))
					continue;
						
				// Metaclass.allInstances()->size() > 0
				OclModelElement me = ASTUtils.createOclModelElement(m);
				
				OperationCallExp call = OCLFactory.eINSTANCE.createOperationCallExp();
				call.setOperationName("allInstances");
				call.setSource(me);
				
				CollectionOperationCallExp size = OCLFactory.eINSTANCE.createCollectionOperationCallExp();
				size.setSource(call);
				size.setOperationName("size");
				
				IntegerExp integer = OCLFactory.eINSTANCE.createIntegerExp();
				integer.setIntegerSymbol(1);
				
				OperatorCallExp operator = OCLFactory.eINSTANCE.createOperatorCallExp();
				operator.setOperationName(">=");
				operator.setSource(size);
				operator.getArguments().add(integer);
				
				expressions.add(operator);
			}
		}
		
		if (expressions.isEmpty()) {
			return Collections.emptyList();
		}
		
		OclExpression exp = ASTUtils.joinExpression(expressions, "and");
		return Collections.singletonList(new ModelConstraint(path, Collections.singletonList(exp)));
	}

	private List<Binding> getReferencingBindings(List<Metaclass> usedMetaclasses) {
		ATLModel atlModel = this.trafo.getATLModel();
		List<? extends MatchedRule> rules = ATLUtils.getAllMatchedRules(atlModel);
		List<? extends Binding> allBindings = atlModel.allObjectsOf(Binding.class);
		
		List<Binding> referencingBindings = new ArrayList<>();
		
		for (Metaclass metaclass : usedMetaclasses) {
			// Lookup the rules to see if we need to include metaclass from rules which
			// reference this metaclass through bindings
			for (MatchedRule matchedRule : rules) {
				Metaclass ruleType = ATLUtils.getInPatternType(matchedRule);
				if (TypeUtils.isClassAssignableTo(metaclass.getKlass(), ruleType.getKlass())) {
					// The rule matches objects of type metaclass
					for (Binding b : allBindings) {
						for (RuleResolutionInfo rri : b.getResolvedBy()) {
							if (rri.getRule() == matchedRule) {
								referencingBindings.add(b);
							}
						}
					}
				}
			}
		}
		
		return referencingBindings;
	}

	private List<Metaclass> getUsedMetaclasses(OclExpression path) {
		List<Metaclass> usedMetaclasses = new ArrayList<Metaclass>();
		path.eAllContents().forEachRemaining(obj -> {
			if (obj instanceof OclModelElement) {
				OclModelElement elem = (OclModelElement) obj;
				Metaclass mm = (Metaclass) elem.getInferredType();
				if (mm != null)
					usedMetaclasses.add(mm);
			}
		});
		
		return usedMetaclasses;
	}
	
	/*
	// This is a rule-based approach
	// We try to force the existence of objects from rules which "call" the rule of interest (represented by its path condition)
	private List<ModelConstraint> getModelVariations_RuleBased(@NonNull OclExpression path, List<? extends Metamodel> metamodels) {
//		for (Metamodel metamodel : metamodels) {
//			new TrafoMetamodelData()
//			
//		}

		// Simple footprint computation
		List<Metaclass> usedMetaclasses = new ArrayList<Metaclass>();
		path.eAllContents().forEachRemaining(obj -> {
			if (obj instanceof OclModelElement) {
				OclModelElement elem = (OclModelElement) obj;
				Metaclass mm = (Metaclass) elem.getInferredType();
				if (mm != null)
					usedMetaclasses.add(mm);
			}
		});
		
		// Possible use EClass directly
		
		ATLModel atlModel = this.trafo.getATLModel();
		List<? extends MatchedRule> rules = ATLUtils.getAllMatchedRules(atlModel);
		List<? extends Binding> allBindings = atlModel.allObjectsOf(Binding.class);
		
		List<Binding> referencingBindings = new ArrayList<>();
		
		for (Metaclass metaclass : usedMetaclasses) {
			// Lookup the rules to see if we need to include metaclass from rules which
			// reference this metaclass through bindings
			for (MatchedRule matchedRule : rules) {
				Metaclass ruleType = ATLUtils.getInPatternType(matchedRule);
				if (TypeUtils.isClassAssignableTo(metaclass.getKlass(), ruleType.getKlass())) {
					// The rule matches objects of type metaclass
					for (Binding b : allBindings) {
						for (RuleResolutionInfo rri : b.getResolvedBy()) {
							if (rri.getRule() == matchedRule) {
								referencingBindings.add(b);
							}
						}
					}
				}
			}
			
			OclModelElement me = OCLFactory.eINSTANCE.createOclModelElement();
			me.setInferredType(metaclass);
		}
		
		// This should recursively build sets of path conditions that covers more and more rule bindings...
		for (Binding binding : referencingBindings) {
			CSPModel cspModel = new CSPModel();
			Rule rule = binding.getOutPatternElement().getOutPattern().getRule();
			
			AbstractBindingAssignmentNode.genValueRightPart(cspModel, binding.getValue());			
		}
		
	}
	*/

	private void addRequiredHelpers(@NonNull OclExpression path, Set<Helper> helpers) {
		TreeIterator<EObject> it = path.eAllContents();
		while ( it.hasNext() ) {
			EObject obj = it.next();
			if ( obj instanceof PropertyCallExp ) {
				List<Helper> resolvers = new ArrayList<Helper>( ((PropertyCallExp) obj).getDynamicResolvers() );
				if ( ((PropertyCallExp) obj).getStaticResolver() instanceof Helper ) {
					resolvers.add((Helper) ((PropertyCallExp) obj).getStaticResolver());
				}
				
				for (Helper helper : resolvers) {
					if ( helpers.contains(helper) )
						continue;

					helpers.add(helper);
					
					OclExpression body = ATLUtils.getHelperBody(helper);
					addRequiredHelpers(body, helpers);
				}
				
			}
		}		
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
			} catch ( Exception e ) {
				System.out.println("Internal error" + e);
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
