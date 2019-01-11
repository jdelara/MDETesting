package anatlyzer.testing.common;

import org.eclipse.jdt.annotation.NonNull;

public interface ITransformationLauncher {

	void exec() throws TransformationExecutionError;

	/**
	 * Returns output model by name.
	 * This is only available after {@link #exec()}
	 */
	@NonNull
	IModel getOutput(@NonNull String modelName);

	@SuppressWarnings("serial")
	public static class TransformationExecutionError extends Exception {
		public TransformationExecutionError(Throwable t) {
			super(t);
		}		
	}
}
