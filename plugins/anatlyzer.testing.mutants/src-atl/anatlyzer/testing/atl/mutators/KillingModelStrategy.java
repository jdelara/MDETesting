package anatlyzer.testing.atl.mutators;

import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import anatlyzer.atl.analyser.AnalysisResult;
import anatlyzer.atl.analyser.IAnalyserResult;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.tests.api.AnalysisLoader;
import anatlyzer.atl.witness.IWitnessFinder;
import anatlyzer.atlext.OCL.OclExpression;
import anatlyzer.testing.common.IProgressMonitor;
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

		ATLModel ast = atlModel.copyAST();
		AnalysisLoader loader = AnalysisLoader.fromATLModel(ast, result.getNamespaces());
		AnalysisResult r = loader.analyse();
		
		if (info.getMutatedElement() == null)
			throw new IllegalStateException();
		
		if (! (info instanceof OclExpression)) {
			return null;
		}

		if ( reference != null ) {			
			IStorageStrategy strategy = factory.apply((AtlMutantReference) reference);
			
			PathBasedModelGenerator generator = new PathBasedModelGenerator(r.getAnalyser(), strategy, finder);
			generator.generateModels((@NonNull OclExpression) info.getMutatedElement(), IProgressMonitor.NULL);
		}
		return reference;
	}

}
