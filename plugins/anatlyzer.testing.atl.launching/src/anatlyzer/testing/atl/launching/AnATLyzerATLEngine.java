package anatlyzer.testing.atl.launching;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.IExtractor;
import org.eclipse.m2m.atl.core.IInjector;
import org.eclipse.m2m.atl.core.IModel;
import org.eclipse.m2m.atl.core.IReferenceModel;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFExtractor;
import org.eclipse.m2m.atl.core.emf.EMFInjector;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.launch.ILauncher;
import org.eclipse.m2m.atl.core.service.CoreService;
import org.eclipse.m2m.atl.engine.emfvm.launch.EMFVMLauncher;

public class AnATLyzerATLEngine {
	
	private Object trafo      = null;
	private IModel source     = null;   // source model
	private IModel target     = null;   // target model
	private String sourceFile = null;   // source model
	private String targetFile = null;   // target model

	private IInjector    injector  = null;
	private IExtractor   extractor = null;
	private ModelFactory factory   = null;
	private ILauncher    launcher  = null;
	
	private void init () {
		try {
			CoreService.registerLauncher ("EMF-specific VM", EMFVMLauncher.class);
			CoreService.registerFactory  ("EMF", EMFModelFactory.class); 
			CoreService.registerExtractor("EMF", EMFExtractor.class);
			CoreService.registerInjector ("EMF", EMFInjector.class);
			injector  = CoreService.getInjector("EMF"); 
			extractor = CoreService.getExtractor("EMF"); 			
			factory   = CoreService.getModelFactory("EMF"); 
			launcher = (EMFVMLauncher)CoreService.getLauncher("EMF-specific VM"); 
			launcher.initialize(Collections.<String, Object> emptyMap());
		} catch (ATLCoreException e) { 
			throw new RuntimeException(e); 
		}
	}

	public void loadTransformation(String URI) throws IOException {
		File uri = new File(URI);
		if (!uri.exists())
			throw new IOException("File not found " + uri);
		
		try {
			URL asmFile = uri.toURI().toURL(); // this is my dirty (?) way to transform a relative path (inside the project) to an absolute path
			trafo       = launcher.loadModule(asmFile.openStream());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

	public void loadSourcemodel(String name, String model, String metamodel) {
		sourceFile = normalizePath(model);
		try {
			IReferenceModel mm = loadmetamodel(metamodel);
			source             = factory.newModel(mm); 
			launcher.addInModel(source, "IN", name);
		} catch (ATLCoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void loadTargetmodel(String name, String model, String metamodel) {
		targetFile = normalizePath(model);
		try {
			IReferenceModel mm = loadmetamodel(metamodel); 
			target             = factory.newModel(mm);
			launcher.addOutModel(target, "OUT", name);
		} catch (ATLCoreException e) {
			throw new RuntimeException(e);
		}
	}

	private IReferenceModel loadmetamodel(String metamodel) {
		if (factory==null) this.init();
		try {
			IReferenceModel mm = factory.newReferenceModel();
			injector.inject(mm, metamodel);
			return mm;
        } 
		catch (ATLCoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean execute() throws ATLCoreException {
		return execute(true);
	}
	
	public boolean execute (boolean allowInterModelReferences) throws ATLCoreException {
		if (trafo==null || source==null || target==null || launcher==null) return false;

		Map<String,Object> options = new HashMap<String,Object>();
		if (allowInterModelReferences) options.put("allowInterModelReferences","true");

		injector.inject  (source, sourceFile);
		launcher.launch(ILauncher.RUN_MODE, new NullProgressMonitor(), options, trafo);
		extractor.extract(target, targetFile);	
		// clear launcher for next execution
		injector  = CoreService.getInjector("EMF"); 
		extractor = CoreService.getExtractor("EMF"); 			
		factory   = CoreService.getModelFactory("EMF"); 
		launcher = (EMFVMLauncher)CoreService.getLauncher("EMF-specific VM"); 
		launcher.initialize(Collections.<String, Object> emptyMap());			
		
		return true;
	}	
	
	private String normalizePath (String path) {
		File f = new File(path);
		if (f.isAbsolute() && !f.getAbsolutePath().startsWith("file:/")) return "file:/"+path;
		return path;
	}	
}
