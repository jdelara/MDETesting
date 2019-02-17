package anatlyzer.testing.atl.common;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.jdt.annotation.NonNull;

import anatlyzer.testing.atl.launching.ATLExecutor;
import anatlyzer.testing.atl.launching.ATLExecutor.ModelData;
import anatlyzer.testing.common.IModel;
import anatlyzer.testing.common.ITransformationLauncher;
import anatlyzer.testing.common.Metamodel;
import anatlyzer.testing.common.Model;

public class AtlLauncher implements ITransformationLauncher {

	private ATLExecutor executor;
	private AtlTransformation transformation;

	public AtlLauncher(@NonNull ATLExecutor executor, @NonNull AtlTransformation t) {
		this.executor = executor;
		this.transformation = t;
	}

	@Override
	public void exec() throws TransformationExecutionError {
		try {
			executor.perform(transformation.getFileName());
		} catch (IOException e) {
			throw new TransformationExecutionError(e);
		}
	}

	@Override
	public IModel getOutput(String modelName) {
		Resource r = executor.getModelResource(modelName);
		ModelData modelData = executor.getModelData(modelName);

		// In ATL a resource maybe null if the transformation doesn't generate any model element
		if ( r == null ) {
			r = new XMIResourceImpl(URI.createURI("file:/" + modelData.getModelPath()));
		}
		
		Metamodel mm = transformation.getTargets().stream().
			filter(t -> t.getModelName().equals(modelName)).
			findAny().
			get().getMetamodel();
		
		
		Model m = new Model(r, mm);
		m.addAttribute(File.class, new File(modelData.getModelPath()));
		return m;
	}

}
