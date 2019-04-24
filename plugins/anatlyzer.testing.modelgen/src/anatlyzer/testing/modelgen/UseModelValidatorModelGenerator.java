package anatlyzer.testing.modelgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

import anatlyzer.atl.errors.ProblemStatus;
import anatlyzer.atl.util.AnalyserUtils;
import anatlyzer.atl.witness.ConstraintSatisfactionChecker;
import anatlyzer.atl.witness.IWitnessFinder;
import anatlyzer.atl.witness.IWitnessFinder.WitnessGenerationMode;
import anatlyzer.atlext.OCL.BooleanExp;
import anatlyzer.atlext.OCL.OCLFactory;
import anatlyzer.testing.common.IProgressMonitor;
import anatlyzer.testing.common.Metamodel;

/**
 * 
 * Model generator based on the USE model validator.
 * 
 * @author jesus
 *
 */
public class UseModelValidatorModelGenerator extends AbstractModelGenerator implements IModelGenerator {

	public static ModelGenerationStrategy getStrategy(Metamodel metamodel, ModelGenerationStrategy.STRATEGY strategy) {
		return getStrategy(metamodel, null, strategy);
	}
	
	public static ModelGenerationStrategy getStrategy(Metamodel metamodel, Metamodel viewFilter, ModelGenerationStrategy.STRATEGY strategy) {
		List<String>     classes    = new ArrayList<String>();
		List<String>     references = new ArrayList<String>();
		List<EReference> auxref     = new ArrayList<EReference>();
		for (EClassifier classifier : metamodel.getEClassifiers()) {
			if ( viewFilter != null ) {
				boolean found = false;
				for (EClassifier filterEClassifier : viewFilter.getEClassifiers()) {
					//String qNameFilter = filterEClassifier.getEPackage().getName() + "::" + filterEClassifier.getName();
					//String qName = classifier.getEPackage().getName() + "::" + classifier.getName();
					// if ( qName.equals(qNameFilter) ) {
					// This is suboptimal but won't produce really bad results even if there are equal names in different packages
					// The problem is that we can't guarantee that our prunner outputs the same package name for the footprint
					if ( filterEClassifier.getName().equals(classifier.getName()) ) {
						found = true;
						break;
					}
				}
				
				if ( ! found ) {
					continue;
				}
			}
			
			if (classifier instanceof EClass) {
				if (!((EClass)classifier).isAbstract()) 
					classes.add(classifier.getName());
				for (EReference ref : ((EClass)classifier).getEReferences()) {
					// optimization: do not consider opposite
					if (!auxref.contains(ref.getEOpposite()))
						references.add(((EClass)classifier).getName()+"."+ref.getName());
					auxref.add(ref);
				}
			}
		}
		
		switch(strategy) {
		case Full:
			return new FullModelGenerationStrategy(classes, references);
		case Lite:
			return new LiteModelGenerationStrategy(classes, references);		
		}
		
		throw new IllegalStateException();
	}

	private Metamodel metamodel;
	private ModelGenerationStrategy.STRATEGY modelStrategy;
	private Metamodel metamodelViewFilter;

	public UseModelValidatorModelGenerator(Metamodel m, ModelGenerationStrategy.STRATEGY modelStrategy, IStorageStrategy strategy, IWitnessFinder wf) {
		super(strategy, wf);
		this.metamodel = m;
		this.modelStrategy = modelStrategy;
	}
	
	public UseModelValidatorModelGenerator(Resource r, ModelGenerationStrategy.STRATEGY modelStrategy, IStorageStrategy strategy, IWitnessFinder wf) {
		super(strategy, wf);
		this.metamodel = new Metamodel(r);
		this.modelStrategy = modelStrategy;
	}
		
	@Override
	public List<IGeneratedModelReference> generateModels(IProgressMonitor monitor) {
		ModelGenerationStrategy useStrategy = getStrategy(metamodel, metamodelViewFilter, modelStrategy);
		
		List<IGeneratedModelReference> generated = new ArrayList<IGeneratedModelReference>(); 
		
		// TODO: This is weird, because useStrategy is an iterator itself, so it is one-shot
		for (Properties propertiesUse : useStrategy) {
			if ( monitor != null && monitor.isCancelled() )
				break;
			
			if ( metamodelViewFilter != null ) {
				wf.setWitnessGenerationModel(WitnessGenerationMode.VIEW_METAMODEL);
			} else {
				wf.setWitnessGenerationModel(WitnessGenerationMode.FULL_METAMODEL);
			}
			
			wf.setScopeCalculator(new GenStrategyScope(propertiesUse));
			
			// PossibleInvariantViolationNode postcondition = postconditionGenerator.get();
			// TODO: I can pass an OCL condition which must be satisfied
			
			BooleanExp exp = OCLFactory.eINSTANCE.createBooleanExp();
			exp.setBooleanSymbol(true);
			
			ConstraintSatisfactionChecker checker = ConstraintSatisfactionChecker.
					withExpr(exp).
					withFinder(wf);
			
			Set<Resource> resources = metamodel.getPackages().stream().map(p -> p.eResource()).collect(Collectors.toSet());
			int i = 1;
			for (Resource r : resources) {
				checker.configureMetamodel("MM" + i, r);				
				i++;
			}
			
			checker.check();
			
			
			ProblemStatus result = checker.getFinderResult();
			// ProblemStatus result = wf.find(constraint, new AnalysisResult((Analyser) postcondition.getAnalysis()));
			
			if ( AnalyserUtils.isConfirmed(result) ) {
				IGeneratedModelReference ref = storageStrategy.save(wf.getFoundWitnessModel(), metamodel);
				generated.add(ref);
			} else if ( AnalyserUtils.isDiscarded(result)) {
				System.out.println("[NO_MODEL_FOUND]: " + result );
			} else {
				System.out.println("Cannot generate model: " + result );
			}

			System.out.println("Processed model with result: " + result);
			if ( monitor != null )
				monitor.workDone("Processed model with result: " + result, 1);

			if ( limit != -1 && generated.size() > limit ) {
				break;
			}
		}
		
		return generated;		
	}

	// This gives a meta-model view (typically a footprint) to only generate models for this subset
	public AbstractModelGenerator withMetamodelViewFilter(Metamodel filter) {
		this.metamodelViewFilter = filter;
		return this;
	}

	/**
	 * Represents an OCL constraint to be used as a constraint that
	 * the model generator must satisfy.
	 * 
	 * @author jesus
	 */
// NOT NEEDED!	
//	public static class ModelGenerationConstraint implements IDetectedProblem {
//
//		private OclExpression expression;
//
//		public ModelGenerationConstraint(OclExpression expression) {
//			this.expression = expression;
//		}
//		
//		public static ModelGenerationConstraint getNoConstraint() {
//			BooleanExp exp = OCLFactory.eINSTANCE.createBooleanExp();
//			exp.setBooleanSymbol(true);
//			return new ModelGenerationConstraint(exp);
//		}
//		
//		@Override
//		public ErrorSlice getErrorSlice(IAnalyserResult result) {
//			ErrorSlice slice = new ErrorSlice(result, ATLUtils.getSourceMetamodelNames(result.getATLModel()), this);			
//			OclSlice.slice(slice, expression);
//			return slice;
//		}
//
//		@Override
//		public OclExpression getWitnessCondition() {
//			return expression;
//		}
//
//		@Override
//		public boolean isExpressionInPath(OclExpression expr) {
//			return false;
//		}
//
//		@Override
//		public List<OclExpression> getFrameConditions() {
//			return Collections.emptyList();
//		}
//		
//	}

}
