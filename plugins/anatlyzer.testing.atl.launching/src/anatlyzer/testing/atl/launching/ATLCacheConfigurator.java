package anatlyzer.testing.atl.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.jdt.annotation.NonNull;

import anatlyzer.atl.util.Pair;
import anatlyzer.testing.atl.common.AtlLauncher;
import anatlyzer.testing.atl.common.AtlTransformation;
import anatlyzer.testing.atl.launching.ATLExecutor.ModelData;
import anatlyzer.testing.common.IModel;
import anatlyzer.testing.common.ITransformation.ModelSpec;
import anatlyzer.testing.common.ITransformationLauncher;
import anatlyzer.testing.common.Metamodel;
import anatlyzer.testing.common.Model;

public class ATLCacheConfigurator extends ATLDefaultConfigurator {
	
	@Override
	public AtlLauncher configure(AtlTransformation t, IModel... inputs) {
		Map<String, Pair<String, ModelSpec>> existing = new HashMap<>();
		for (ModelSpec m : t.getTargets()) {
			if ( ! outputPaths.containsKey(m.getModelName()) ) {
				throw new IllegalArgumentException();
			}
			
			String path = outputPaths.get(m.getModelName()).apply(t, inputs).getAbsolutePath();
			if ( ! new File(path).exists() )
				return super.configure(t, inputs);
			
			existing.put(m.getModelName(), new Pair<String, ModelSpec>(path, m));
		}

		return new NoExecLauncher(t, existing);
	}
	
	private static class NoExecLauncher extends AtlLauncher {

		private Map<String, Pair<String, ModelSpec>> existing;

		public NoExecLauncher(@NonNull AtlTransformation t, Map<String, Pair<String, ModelSpec>> existing2) {
			super(null, t);
			this.existing = existing2;
		}

		@Override
		public void exec() throws TransformationExecutionError {
			System.out.println("Cached");
		}

		@Override
		public @NonNull IModel getOutput(@NonNull String modelName) {
			Pair<String, ModelSpec> pair = this.existing.get(modelName);
			ResourceSet rs = new ResourceSetImpl();
			for(EPackage p : pair._2.getMetamodel().getPackages()) {
				rs.getPackageRegistry().put(p.getNsURI(), p);				
			}
			
			Resource r = rs.getResource(URI.createFileURI(pair._1), true);
			
			Model m = new Model(r, pair._2.getMetamodel());
			m.addAttribute(File.class, new File(pair._1));
			return m;
		}
		
	}
	
}
