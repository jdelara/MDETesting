package anatlyzer.testing.atl.common;

import java.io.IOException;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.annotation.NonNull;

import anatlyzer.testing.atl.launching.ATLExecutor;
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
		Metamodel mm = transformation.getTargets().stream().
			filter(t -> t.getModelName().equals(modelName)).
			findAny().
			get().getMetamodel();
		return new Model(r, mm);
	}

}
