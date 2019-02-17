package anatlyzer.testing.mutants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.m2m.atl.core.ATLCoreException;

import anatlyzer.atl.analyser.namespaces.GlobalNamespace;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.tests.api.AnalysisLoader;
import anatlyzer.atl.util.ATLUtils;
import anatlyzer.atl.util.ATLUtils.ModelInfo;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.atl.mutators.IMutatorRegistry;
import anatlyzer.testing.atl.mutators.IStorageStrategy;
import anatlyzer.testing.common.IProgressMonitor;
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
	private String folderMutants;
	private IStorageStrategy strategy;
	private IMutatorRegistry mutatorRegistry = new IMutatorRegistry.AllMutators();
	private int limit = -1;
	
	public ATLMutantGenerator(AnalysisLoader loader, String folderMutants) throws ATLCoreException {
		this(loader.getAtlTransformation(), loader.getNamespace(), folderMutants);
	}
	
	public ATLMutantGenerator(ATLModel atlModel, GlobalNamespace namespace, String folderMutants) {
		this.atlModel = atlModel;
		this.namespace = namespace;
		this.folderMutants = folderMutants;

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
	
//	/**
//	 * @param trafo transformation to be used in the evaluation
//	 * @param temporalFolder temporal folder used to store the generated mutants and input test models
//	 * @param strategy model generation strategy (Lite by default) 
//	 * @throws ATLCoreException 
//	 * @throws transException 
//	 */
//	public Tester (String trafo, String temporalFolder) throws ATLCoreException, transException { this (trafo, temporalFolder, ModelGenerationStrategy.STRATEGY.Lite);	}
//	public Tester (String trafo, String temporalFolder, ModelGenerationStrategy.STRATEGY strategy) throws ATLCoreException, transException {
//		this.rs       = new ResourceSetImpl();
//		this.report   = new Report();
//		this.atlFile  = trafo;
//		this.atlModel = this.loadTransformationModel(trafo);
//		this.loadMetamodelsFromTransformation();
//		this.modelGenerationStrategy = strategy;
//		// initialize temporal folders
//		this.folderMutants = temporalFolder + "mutants" + File.separator;
//		this.folderModels  = temporalFolder + "testmodels" + File.separator;
//		this.folderTemp    = temporalFolder + "temp" + File.separator;
//	}
	
	
//	public void setGenerateMutants(boolean optionGenerateNewMutants) {
//		this.generateMutants = optionGenerateNewMutants;
//	}
//	
//	public void setGenerateTestModels(boolean optionGenerateTestModels) {
//		this.generateTestModels = optionGenerateTestModels;
//	}

	
	/**
	 * It generates mutants of a transformation.
	 * @throws transException 
	 * @throws ATLCoreException 
	 */
	public void generateMutants(IProgressMonitor monitor) {
		MuMetaModel iMetaModel, oMetaModel;
		
		// TODO: Generate mutants using all meta-models, perhaps organizing them in source/target mutants
		
		iMetaModel = new MuMetaModel(new ArrayList<EPackage>(this.namespace.getNamespace(this.inputMetamodels.get(0)).getLoadedPackages()));
		oMetaModel = new MuMetaModel(new ArrayList<EPackage>(this.namespace.getNamespace(this.outputMetamodels.get(0)).getLoadedPackages()));
		iMetaModel.setName (this.namespace.getNamespace(this.inputMetamodels.get (0)).getName());
		oMetaModel.setName (this.namespace.getNamespace(this.outputMetamodels.get(0)).getName());
		
		List<? extends AbstractMutator> operators = mutatorRegistry.getMutators();
		IStorageStrategy strategy = this.strategy == null ? 
				new IStorageStrategy.FileBasedStartegy(this.folderMutants) :
				this.strategy;
		
		monitor.beginWork("Generating mutants", operators.size());
		for (AbstractMutator operator : operators) {
			if ( monitor.isCancelled() )
				break;
				
			operator.setStorageStrategy(strategy);
			monitor.beginWork("Generating mutant: " + operator.getClass().getSimpleName(), 1);
			operator.generateMutants(atlModel, iMetaModel, oMetaModel);
			monitor.workDone("Generated", 1);
		}
		monitor.workDone("Generated mutants", operators.size());
		
	}

	/**
	 * Computes the time in minutes since the evaluation started.
	 */
	public long getElapsedTime() {
		long diff = System.currentTimeMillis() - initTime;
		return diff / (1000 * 60);
	}
	
}
