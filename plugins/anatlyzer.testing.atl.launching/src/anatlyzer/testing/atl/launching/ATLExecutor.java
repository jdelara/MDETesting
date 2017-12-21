package anatlyzer.testing.atl.launching;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.IExtractor;
import org.eclipse.m2m.atl.core.IModel;
import org.eclipse.m2m.atl.core.IReferenceModel;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFExtractor;
import org.eclipse.m2m.atl.core.emf.EMFInjector;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.launch.ILauncher;
import org.eclipse.m2m.atl.engine.compiler.AtlStandaloneCompiler;
import org.eclipse.m2m.atl.engine.compiler.CompileTimeError;
import org.eclipse.m2m.atl.engine.compiler.atl2006.Atl2006Compiler;
import org.eclipse.m2m.atl.engine.emfvm.launch.EMFVMLauncher;

public class ATLExecutor {
	
	/**
	 * The refining trace model.
	 */
	protected IModel refiningTraceModel;

	private String transformationFile;
	private String temporalAsmPath = null;
	
	private ArrayList<ModelData> modelData;

	private ILauncher launcher;
	
	public static class ModelData {
		public final String metamodelName;
		public final String metamodelPath;
		private String modelName;
		public final String modelPath;
		private IReferenceModel loadedMetamodel;
		private IModel loadedModel;
		private ModelKind kind;
		private String newModelPath;
		private Resource inputResource;
		
		public ModelData(String modelName, String modelPath, String metamodelName, String metamodelPath, ModelKind kind) {
			this.metamodelName = metamodelName;
			this.metamodelPath = metamodelPath;
			this.modelName = modelName;
			this.modelPath = modelPath;    // This what it is read
			this.newModelPath = modelPath; // This is for serialization
			this.kind = kind;			
		}

		public ModelData(String modelName, String modelPath, String metamodelName, String metamodelPath, ModelKind kind, String newModelPath) {
			this(modelName, modelPath, metamodelName, metamodelPath, kind);
			if ( kind != ModelKind.INOUT )
				throw new IllegalArgumentException();
			this.newModelPath = newModelPath;
		}
		
		public ModelData(String modelName, Resource resource, String metamodelName, String metamodelPath, ModelKind kind) {
			this(modelName, "no-model", metamodelName, metamodelPath, kind);
			this.inputResource = resource;
		}

		public void load() {
			try {
				ModelFactory factory = new EMFModelFactory();
				EMFInjector injector = new EMFInjector();
			 	loadedMetamodel = factory.newReferenceModel();
				injector.inject(loadedMetamodel, metamodelPath);
				this.loadedModel = factory.newModel(loadedMetamodel);
				if ( kind != ModelKind.OUT) {
					if ( inputResource != null ) {
						injector.inject(loadedModel, inputResource);
					} else {
						injector.inject(loadedModel, modelPath);
					}
				}
			} catch ( ATLCoreException e ) {
				throw new RuntimeException(e);
			}
		}

		public void save() throws ATLCoreException {
			IExtractor extractor = new EMFExtractor();
			extractor.extract(this.loadedModel, this.newModelPath);
		}
	}
	
	public static enum ModelKind {
		IN,
		OUT,
		INOUT
	}
	
	public static ModelData inModel(String modelName,  String modelPath, String metamodelName, String metamodelPath) {
		return new ModelData(modelName, modelPath, metamodelName, metamodelPath, ModelKind.IN);
	}

	public static ModelData inModel(String modelName,  Resource resource, String metamodelName, String metamodelPath) {
		return new ModelData(modelName, resource, metamodelName, metamodelPath, ModelKind.IN);
	}

	public static ModelData outModel(String modelName,  String modelPath, String metamodelName, String metamodelPath) {
		return new ModelData(modelName, modelPath, metamodelName, metamodelPath, ModelKind.OUT);
	}

	public static ModelData inOutModel(String modelName,  String modelPath, String metamodelName, String metamodelPath, String newModelPath) {
		return new ModelData(modelName, modelPath, metamodelName, metamodelPath, ModelKind.INOUT, newModelPath);
	}


	private boolean allowInterModelReferences;

	private boolean doModelWarmup;

	
	public ATLExecutor allowInterModelReferences(boolean v) {
		this.allowInterModelReferences = v;
		return this;
	}

	
	public ATLExecutor perform(String transformationFile, ModelData... models) throws IOException {	

		this.transformationFile = transformationFile;
		transformationFile = normalizePath(transformationFile);
		String asmFile = compileToASMFile(transformationFile);
		
		return perform(new FileInputStream(asmFile), models);
	}
		
	public ATLExecutor perform(InputStream asmStream, ModelData... models) throws IOException {
		modelData = new ArrayList<ATLExecutor.ModelData>();
		for (ModelData m : models) {
			modelData.add(m);
		}
		

				
		this.launcher = null;
		this.modelData = new ArrayList<ModelData>();
		for (ModelData modelData : models) {
			modelData.load();
			this.modelData.add(modelData);
		}
		
		ILauncher launcher = new EMFVMLauncher();
		Map<String, Object> launcherOptions = new HashMap<String, Object>();
		
		launcherOptions.put("allowInterModelReferences", allowInterModelReferences); // TODO: Allow configuration
		launcher.initialize(launcherOptions);
		
		for (ModelData modelData : models) {
			switch ( modelData.kind ) {
			case IN:
				
				if ( doModelWarmup )
					warmupModelInit(modelData);			
				
				launcher.addInModel(modelData.loadedModel, modelData.modelName, modelData.metamodelName);
				break;
			case INOUT:
				launcher.addInOutModel(modelData.loadedModel, modelData.modelName, modelData.metamodelName);
				break;
			case OUT:
				launcher.addOutModel(modelData.loadedModel, modelData.modelName, modelData.metamodelName);
				break;
			}
		}
		double time0 = System.currentTimeMillis();
		launcher.launch("run", null, launcherOptions, asmStream);
		double timeF = (System.currentTimeMillis() - time0) / 1000;
		System.out.println("ATL time: " + timeF);
		this.launcher = launcher;
		return this;
		// IReferenceModel refiningTraceMetamodel = factory.getBuiltInResource("RefiningTrace.ecore");
		// refiningTraceModel = factory.newModel(refiningTraceMetamodel);

	}
	
	private void warmupModelInit(ModelData modelData) {
		double time0 = System.currentTimeMillis();
		// Warmup to avoid bias
		EMFModel em = ((EMFModel) modelData.loadedModel);
		for (EObject eClass : em.getReferenceModel().getAllElementsByType(EcorePackage.Literals.ECLASS)) {
			em.getElementsByType(eClass);					
		}
		double timeF = (System.currentTimeMillis() - time0) / 1000;
		System.out.println("Input model warmup: " + timeF);
	}

	private String normalizePath(String s) {
		return s.replaceAll("\\\\", "/");
	}

	public ATLExecutor save() throws ATLCoreException, IOException {
				
		// Normal case
		for (ModelData md : modelData) {
			if ( md.kind == ModelKind.OUT || md.kind == ModelKind.INOUT ) {
				md.save();
			}
		}
		return this;
	}
	
	public Resource getModelResource(String name) {
		return getModel(name).getResource();
	}
	
	public EMFModel getModel(String name) {		
		if ( launcher == null )
			throw new IllegalStateException();
		return (EMFModel) launcher.getModel(name);
	}
	
	public ATLExecutor tempAsmPath(String path) {
		this.temporalAsmPath = path;
		return this;
	}
	
		
	private String compileToASMFile(String trafo) throws IOException {
		// compile transformation
		File trafoFile = new File(trafo);		
		String asmTransformation = trafo.replace(".atl", ".asm");
		if ( temporalAsmPath != null ) {
			asmTransformation = temporalAsmPath + File.separator + trafoFile.getName().replace(".atl", ".asm");
			asmTransformation = normalizePath(asmTransformation);
		}
		
		

		// AtlCompiler.getCompiler("atl2006").compile(in, outputFileName)
		//Atl2006Compiler compiler  = new Atl2006Compiler();
		//Atl2010InPlace compiler  = new Atl2010InPlace();
		AtlStandaloneCompiler compiler = null; 
		compiler = new Atl2006Compiler();
		
		FileInputStream fis = new FileInputStream(trafoFile);
		CompileTimeError[] errors = compiler.compile(fis, asmTransformation);
		fis.close();
				
		boolean fatalErrors = false;
		for (CompileTimeError error : errors) fatalErrors = fatalErrors || !error.getSeverity().equals("warning");		
		if  (fatalErrors || !new File(asmTransformation).exists()) {
			System.out.println( fatalErrors? "---> [" + errors[0].getLocation() + "] " + errors[0].getDescription() : "---> no asm file could be generated");
			return null;
		}
			
		return asmTransformation;		
	}

	public void doModelWarmup(boolean modelWarmup) {
		this.doModelWarmup = modelWarmup;
	}


}
