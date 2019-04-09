package anatlyzer.testing.atl.mutators;

import java.util.List;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import anatlyzer.atl.analyser.AnalysisResult;
import anatlyzer.atl.analyser.IAnalyserResult;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.model.ATLModel.CopiedATLModel;
import anatlyzer.atl.tests.api.AnalysisLoader;
import anatlyzer.atl.witness.IWitnessFinder;
import anatlyzer.atlext.ATL.LocatedElement;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.testing.common.IProgressMonitor;
import anatlyzer.testing.modelgen.IGeneratedModelReference;
import anatlyzer.testing.modelgen.IStorageStrategy;
import anatlyzer.testing.modelgen.atl.PathBasedModelGenerator;
import anatlyzer.testing.mutants.AtlMutantReference;
import anatlyzer.testing.mutants.IMutantGenerator.IMutantReference;

/**
 * 
 * The current design has the problem that we link the model generation
 * by generated mutant name (through {@link AtlMutantReference}). 
 * 
 * @author jesus
 *
 */
public class KillingModelStrategy implements anatlyzer.testing.atl.mutators.IStorageStrategy {

	private @NonNull IWitnessFinder finder;
	private IAnalyserResult result;
	private @NonNull Function<AtlMutantReference, IStorageStrategy> factory;
	private anatlyzer.testing.atl.mutators.@NonNull IStorageStrategy mutantStorageStrategy;

	public KillingModelStrategy(@NonNull IAnalyserResult r, @NonNull IWitnessFinder finder, 
			anatlyzer.testing.atl.mutators.@NonNull IStorageStrategy mutantStorageStrategy,
			@NonNull Function<AtlMutantReference, IStorageStrategy> factory) {
		this.finder = finder;
		this.factory = factory;
		this.result = r;
		this.mutantStorageStrategy = mutantStorageStrategy;
	}
	
	@Override
	public IMutantReference save(ATLModel atlModel, MutationInfo info) {
		@Nullable
		IMutantReference reference = mutantStorageStrategy.save(atlModel, info);
		
		System.out.println("Killing model for: " + ((AtlMutantReference) reference).getFile().getName());
		
		CopiedATLModel ast = atlModel.copyAll();
		ast.clear();
		AnalysisLoader loader = AnalysisLoader.fromATLModel(ast, result.getNamespaces());
		AnalysisResult r = loader.analyse();
		
		LocatedElement changed = ((@NonNull LocatedElement) info.getMutatedElement());
		if (changed == null)
			throw new IllegalStateException();
		
		if ( reference != null ) {
			LocatedElement element ;
			
			IStorageStrategy strategy = factory.apply((AtlMutantReference) reference);

			try {
				switch(info.getKind()) {
				case ADD:
				case CHANGE:
					element = (LocatedElement) ast.getTarget(changed);
	
					PathBasedModelGenerator generator = new PathBasedModelGenerator(r.getAnalyser(), strategy, finder);
					generator.generateModels(element, IProgressMonitor.NULL);				
					break;
				case REMOVE:
					// Not supported
					break;
				default:
					break;
				}	
			} catch (Exception e) {
				e.printStackTrace();
				// Record in an error report or strategy that we can't generate
				// due to an internal error
				// This is likely not a bug in the implementation, but some mutants
				// breaks the original trafo., and we are trying to generate path conditions
				// in broken trafos.
			}
		}
		
		return reference;
	}

}
