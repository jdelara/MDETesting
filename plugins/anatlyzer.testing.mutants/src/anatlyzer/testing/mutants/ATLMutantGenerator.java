package anatlyzer.testing.mutants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFReferenceModel;
import org.eclipse.m2m.atl.engine.parser.AtlParser;

import transML.exceptions.transException;
import anatlyzer.atl.analyser.Analyser;
import anatlyzer.atl.analyser.namespaces.GlobalNamespace;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atl.tests.api.AnalysisLoader;
import anatlyzer.atl.util.ATLUtils;
import anatlyzer.atl.util.ATLUtils.ModelInfo;
import anatlyzer.atl.util.AnalyserUtils;
import anatlyzer.atl.util.AnalyserUtils.IAtlFileLoader;
import anatlyzer.testing.atl.mutators.AbstractMutator;
import anatlyzer.testing.atl.mutators.IMutatorRegistry;
import anatlyzer.testing.atl.mutators.IStorageStrategy;
import anatlyzer.testing.atl.mutators.IStorageStrategy.FileBasedStartegy;
import anatlyzer.ui.util.AtlEngineUtils;

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
	
	public ATLMutantGenerator(String atlFile, GlobalNamespace namespace, String folderMutants) throws ATLCoreException {
		this(loadTransformationModel(atlFile), namespace, folderMutants);
	}
	
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
		
		// create output folder
		this.deleteDirectory(this.folderMutants, true);
		this.createDirectory(this.folderMutants);
		
		List<? extends AbstractMutator> operators = new IMutatorRegistry.AllMutators().getMutators();
		IStorageStrategy strategy = this.strategy == null ? 
				new IStorageStrategy.FileBasedStartegy(this.folderMutants) :
				this.strategy;
		
		monitor.beginTask("Generating mutants", operators.size());
		for (AbstractMutator operator : operators) {
			if ( monitor.isCanceled() )
				break;
				
			operator.setStorageStrategy(strategy);
			operator.generateMutants(atlModel, iMetaModel, oMetaModel);
			monitor.worked(1);
		}
		
	}

	
// THIS IS FOR MODEL GENERATION!	
	
//	/**
//	 * It generates instances of the input metamodel.
//	 * @throws transException 
//	 * @throws IOException 
//	 */
//	private void generateTestModels () throws transException, IOException {
//		String metamodelName = this.inputMetamodels.get(0);
//		Resource resource    = this.namespace.getLogicalNamesToMetamodels().get(metamodelName);
//		// TODO: consider several input models
//		EPackage metamodel   = null;
//		for (EObject obj : resource.getContents()) {
//			if (obj instanceof EPackage && ((EPackage)obj).getName().equals(metamodelName)) {						
//				metamodel = (EPackage)obj;
//				break;
//			}
//		}
//		if (metamodel==null) metamodel = (EPackage)resource.getContents().get(0); // TODO: fix
//		if (metamodel.getNsURI()==null) metamodel.setNsURI(aliasToPaths.get(metamodel.getName()).getURIorPath());
//		
//		// create temporal and output folders
//		this.deleteDirectory(this.folderTemp, true);
//		this.deleteDirectory(this.folderModels, true);
//		this.createDirectory(this.folderTemp);
//		
//		// build arrays with the name of classes and references; they will be used to generate 
//		// the scope for the number of objects for each class, and the number of links for each
//		// reference, which will be different in each generated model.
//		List<String>     classes    = new ArrayList<String>();
//		List<String>     references = new ArrayList<String>();
//		List<EReference> auxref     = new ArrayList<EReference>();
//		for (EClassifier classifier : metamodel.getEClassifiers()) {
//			if (classifier instanceof EClass) {
//				if (!((EClass)classifier).isAbstract()) 
//					classes.add(classifier.getName());
//				for (EReference ref : ((EClass)classifier).getEReferences()) {
//					// optimization: do not consider opposite
//					if (!auxref.contains(ref.getEOpposite()))
//						references.add(((EClass)classifier).getName()+"."+ref.getName());
//					auxref.add(ref);
//				}
//			}
//		}
//		
//		// initialize parameters for model generation
//		Properties properties = new Properties(); 
//		saveTransMLProperties(properties);
//		
//		// load transformation preconditions (defined as comments annotated by @pre)
//		Module       module        = new ATLModel(atlModel.getResource()).getModule();
//		List<String> preconditions = new ArrayList<String>();
//		String tag = "@pre_use_format";
//		for (String s : module.getCommentsBefore()) {
//			if (s.contains(tag)) 
//				preconditions.add(s.substring( s.indexOf(tag)+tag.length()).trim());
//		}
//		
//		// generate models
//		SolverWrapper solver = FactorySolver.getInstance().createSolverWrapper();
//		ModelGenerationStrategy modelGenerationStrategy =
//				this.modelGenerationStrategy == ModelGenerationStrategy.STRATEGY.Full?
//				new FullModelGenerationStrategy(classes, references) :
//				new LiteModelGenerationStrategy(classes, references) ;
//		for (Properties propertiesUse : modelGenerationStrategy) {
//			try {
//				saveTransMLProperties(propertiesUse);
//				String model = solver.find(metamodel, preconditions); // Collections.<String>emptyList());
//				System.out.println("generated model: " + ( model!=null? model : "NONE" ));
//			}
//			catch (transException e) {
//				String error = e.getDetails().length>0? e.getDetails()[0] : e.getMessage();
//				if (error.endsWith("\n")) error = error.substring(0, error.lastIndexOf("\n"));
//				System.out.println("[ERROR] " + error); 
//			}
//		}
//		
//		// move generated models to output folder
//		this.moveDirectory (this.folderTemp + "models", this.folderModels);
//	}
//	
//	/**
//	 * It stores the received properties object as a transML properties file. 
//	 * @throws transException 
//	 */
//	private void saveTransMLProperties(Properties properties) throws transException {
//		try {
//			File file = new File(this.folderTemp, "transml.properties");
//			FileOutputStream fileOut = new FileOutputStream(file);
//    		properties.put("solver", "use");
//			properties.put("solver.scope", "10");
//	    	properties.put("temp", new File(this.folderTemp).getAbsolutePath()); 
//			properties.store(fileOut, "--"); 
//			fileOut.close();
//		}
//		catch (IOException e) { e.printStackTrace(); }
//		transMLProperties.loadPropertiesFile(this.folderTemp);
//	}
	
	
	/**
	 * It loads a transformation as a model.
	 * @param atlTransformationFile 
	 * @return transformation model
	 * @throws ATLCoreException
	 */
	private static ATLModel loadTransformationModel (String atlTransformationFile) throws ATLCoreException {
		ModelFactory      modelFactory = new EMFModelFactory();
		EMFReferenceModel atlMetamodel = (EMFReferenceModel)modelFactory.getBuiltInResource("ATL.ecore");
		AtlParser         atlParser    = new AtlParser();		
		EMFModel          atlModel     = (EMFModel)modelFactory.newModel(atlMetamodel);
		atlParser.inject (atlModel, atlTransformationFile);	
		atlModel.setIsTarget(true);				
		
//		// Should we want to serialize the model.
//		String injectedFile = "file:/" + atlTransformationFile + ".xmi";
//		IExtractor extractor = new EMFExtractor();
//		extractor.extract(atlModel, injectedFile);
		
		return new ATLModel(atlModel.getResource(), atlTransformationFile, true);
	}
//	
//	/**
//	 * It loads the metamodels used by the transformation. The path of the meta-models must
//	 * be defined as comments (starting by -- @path) at the beginning of the transformation.
//	 * @return
//	 */
//	private void loadMetamodelsFromTransformation() throws transException {
//		/*
//		HashMap<String, Resource> logicalNamesToResources = new HashMap<String, Resource>();
//		ArrayList<Resource>       resources               = new ArrayList<Resource>();
//		ATLModel                  wrapper                 = new ATLModel(atlModel.getResource());
//		List<Module>              modules                 = (List<Module>)wrapper.allObjectsOf(Module.class);
//		
//		for (Module module : modules) {
//			
//			// obtain path of meta-models from the transformation
//			for (String comment : module.getCommentsBefore()) {
//				comment = comment.trim();
//				if (comment.startsWith("-- @path")) {
//					comment = comment.substring(8).trim();
//					String[] path = comment.split("=");
//					try {
//						String uri = path[1].trim();
//						this.loadMetamodel(uri);
//						Resource r = rs.getResource(URI.createFileURI(uri), true);
//						resources.add(r);
//						logicalNamesToResources.put(path[0].trim(), r);
//					}
//					catch (Exception e) {
//						throw new transException(transException.ERROR.FILE_NOT_FOUND, path[1].trim());
//					}
//				}
//			}
//			
//			// obtain metamodel of IN models of the transformation
//			for (OclModel model : module.getInModels()) {
//				String metamodel = model.getMetamodel().getName();
//				if (!logicalNamesToResources.containsKey(metamodel))
//					throw new transException(transException.ERROR.GENERIC_ERROR, "Path of metamodel "+metamodel+" not found");
//				else this.inputMetamodels.add(metamodel);
//			}
//			
//			// obtain metamodel of OUT models of the transformation
//			for (OclModel model : module.getOutModels()) {
//				String metamodel = model.getMetamodel().getName();
//				if (!logicalNamesToResources.containsKey(metamodel))
//					throw new transException(transException.ERROR.GENERIC_ERROR, "Path of metamodel "+metamodel+" not found");
//				else this.outputMetamodels.add(metamodel);
//			}
//		}
//		
//		this.namespace = new GlobalNamespace(resources, logicalNamesToResources);
//		*/
//		
//		// register ecore factory
//		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
//		
//		try {
//			ATLModel tmpAtlModel = new ATLModel(atlModel.getResource());
//			this.namespace = AnalyserUtils.prepare(tmpAtlModel, new IAtlFileLoader() {			
//				@Override
//				public Resource load(IFile f) {
//					EMFModel libModel = AtlEngineUtils.loadATLFile(f);
//					return libModel.getResource();
//				}
//
//				@Override
//				public Resource load(String text) {
//					EMFModel libModel = AtlEngineUtils.loadATLText(text);
//					return libModel.getResource();
//				}
//			});
//			
//			for (ModelInfo info : ATLUtils.getModelInfo(tmpAtlModel)) {
//				if (info.isInput()) 
//					 this.inputMetamodels.add (info.getMetamodelName());
//				else this.outputMetamodels.add(info.getMetamodelName());
//				aliasToPaths.put(info.getMetamodelName(), info);
//
//				// register metamodel
//				List<EPackage> metamodels = EMFUtils.loadEcoreMetamodel(info.getURIorPath());
//		        for (EPackage p: metamodels) {
//		        	if (p.getNsURI()!=null && !p.getNsURI().equals("")) rs.getPackageRegistry().put(p.getNsURI(), p);
//		        	if (p.getName().equals(info.getMetamodelName()))    rs.getPackageRegistry().put(info.getMetamodelName(), p);
//		        	
//		        	// assign instance class name to data types (it is empty in uml/kermeta meta-models)
//		        	for (EClassifier classifier : p.getEClassifiers())
//		        		if (classifier instanceof EDataType)
//		        			if (((EDataType)classifier).getInstanceClassName() == null)
//		        				if      (classifier.getName().equals("String"))  ((EDataType)classifier).setInstanceClassName("java.lang.String");
//		        				else if (classifier.getName().equals("Integer")) ((EDataType)classifier).setInstanceClassName("java.lang.Integer");
//		        				else if (classifier.getName().equals("Boolean")) ((EDataType)classifier).setInstanceClassName("java.lang.Boolean");
//		        }
//			}
//		} 
//		catch (CoreException | CannotLoadMetamodel | PreconditionParseError e ) {
//			throw new transException(transException.ERROR.GENERIC_ERROR, e.getMessage());
//		}
//	}
//	
	/**
	 * It deletes a directory.
	 * @param folder name of directory
	 * @param recursive it deletes the subdirectories recursively
	 */
	private void deleteDirectory (String directory, boolean recursive) {
		File folder = new File(directory);
		if (folder.exists())
			for (File file : folder.listFiles()) {				
				if (file.isDirectory()) deleteDirectory(file.getPath(), recursive);
				file.delete();
			}
		folder.delete();
	}
	
	/**
	 * It creates a directory.
	 * @param folder name of directory
	 */
	private void createDirectory (String directory) {
		File folder = new File(directory);
		while (!folder.exists()) 
			folder.mkdir();
	}
	
	/**
	 * It moves a source directory to a target directory.
	 * @param sourceDirectory
	 * @param targetDirectory
	 * @throws IOException
	 */
	private void moveDirectory (String sourceDirectory, String targetDirectory) throws IOException {
		File source = new File(sourceDirectory);
		File target = new File(targetDirectory);
		Files.move(source.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE);
	}
	
	/**
	 * Computes the time in minutes since the evaluation started.
	 */
	public long getElapsedTime() {
		long diff = System.currentTimeMillis() - initTime;
		return diff / (1000 * 60);
	}
	
}
