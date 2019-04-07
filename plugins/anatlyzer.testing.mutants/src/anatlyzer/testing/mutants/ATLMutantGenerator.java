package anatlyzer.testing.mutants;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;

import anatlyzer.atl.analyser.namespaces.GlobalNamespace;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.tests.api.AnalysisLoader;
import anatlyzer.atl.util.ATLUtils;
import anatlyzer.atl.util.ATLUtils.ModelInfo;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.atl.mutators.IMutatorRegistry;
import anatlyzer.testing.atl.mutators.IStorageStrategy;
import anatlyzer.testing.common.IProgressMonitor;
import anatlyzer.testing.mutants.IMutantGenerator.IMutantReference;
import transML.exceptions.transException;

/**
 * Generates mutants storing them in the given folder.
 * 
 * @author jesus
 *
 */
public class ATLMutantGenerator {
	
	private GlobalNamespace namespace; // meta-models used by the transformation (union of inputMetamodels and outputMetamodels)
	private List<String> inputMetamodels  = new ArrayList<String>(); // input metamodels (IN)
	private List<String> outputMetamodels = new ArrayList<String>(); // output metamodels (OUT)
	private ATLModel atlModel;
	private long initTime;
	private IStorageStrategy strategy;
	private IMutatorRegistry mutatorRegistry = new IMutatorRegistry.AllMutators();
	private int limit = -1;
	
	public ATLMutantGenerator(AnalysisLoader loader, String folderMutants) {
		this(loader.getAtlTransformation(), loader.getNamespace(), 
						new IStorageStrategy.FileBasedStartegy(folderMutants));
	}
	
	public ATLMutantGenerator(AnalysisLoader loader, IStorageStrategy strategy) {
		this(loader.getAtlTransformation(), loader.getNamespace(), strategy);
	}
	
	public ATLMutantGenerator(ATLModel atlModel, GlobalNamespace namespace, IStorageStrategy strategy) {
		this.atlModel = atlModel;
		this.namespace = namespace;
		this.strategy = strategy;

		for (ModelInfo modelInfo : ATLUtils.getModelInfo(atlModel)) {
			if ( modelInfo.isInput() ) {
				inputMetamodels.add(modelInfo.getMetamodelName());
			}
			if ( modelInfo.isOutput() ) {
				outputMetamodels.add(modelInfo.getMetamodelName());
			}
		}
		
        // Anatlyse, is this needed????
        //Analyser analyser = new Analyser(namespace, atlModel);
        //analyser.perform();
	}
	
	public ATLMutantGenerator withStorageStrategy(IStorageStrategy strategy) {
		this.strategy = strategy;
		return this;
	}
	
	public ATLMutantGenerator withMutationRegistry(IMutatorRegistry registry) {
		this.mutatorRegistry  = registry;
		return this;
	}
	

	public ATLMutantGenerator withLimit(int limit) {
		if ( limit <= 0 )
			limit = -1;
		this.limit = limit;
		return this;
	}
	
	/**
	 * It generates mutants of a transformation.
	 * @return 
	 * @throws transException 
	 * @throws ATLCoreException 
	 */
	public List<IMutantReference> generateMutants(IProgressMonitor monitor) {
		MuMetaModel iMetaModel, oMetaModel;
		
		// TODO: Generate mutants using all meta-models, perhaps organizing them in source/target mutants
		
		iMetaModel = new MuMetaModel(new ArrayList<EPackage>(this.namespace.getNamespace(this.inputMetamodels.get(0)).getLoadedPackages()));
		oMetaModel = new MuMetaModel(new ArrayList<EPackage>(this.namespace.getNamespace(this.outputMetamodels.get(0)).getLoadedPackages()));
		iMetaModel.setName (this.namespace.getNamespace(this.inputMetamodels.get (0)).getName());
		oMetaModel.setName (this.namespace.getNamespace(this.outputMetamodels.get(0)).getName());
		
		List<? extends AbstractMutator> operators = mutatorRegistry.getMutators();
		
		List<IMutantReference> references = new ArrayList<IMutantGenerator.IMutantReference>();
		
		monitor.beginWork("Generating mutants", operators.size());
		for (AbstractMutator operator : operators) {
			if ( monitor.isCancelled() )
				break;
				
			operator.setStorageStrategy(strategy);
			monitor.beginWork("Generating mutant: " + operator.getClass().getSimpleName(), 1);
			operator.generateMutants(atlModel, iMetaModel, oMetaModel);
			
			references.addAll( operator.getGeneratedMutants() );
			
			monitor.workDone("Generated", 1);
		}
		monitor.workDone("Generated mutants", operators.size());
		
		return references;
	}

	/**
	 * Computes the time in minutes since the evaluation started.
	 */
	public long getElapsedTime() {
		long diff = System.currentTimeMillis() - initTime;
		return diff / (1000 * 60);
	}
	
}
