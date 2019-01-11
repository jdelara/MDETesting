package anatlyzer.testing.common;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Represents a progress monitor which is agnostic of the Eclipse framework.
 * 
 * @author jesus
 *
 */
public interface IProgressMonitor {

	@NonNull
	public static IProgressMonitor NULL = new IProgressMonitor() {

		private boolean cancel;

		@Override
		public void workDone(@NonNull String what) { }

		@Override
		public void cancel() {
			this.cancel = true;
		}

		@Override
		public boolean isCancelled() {
			return cancel;
		}
		
	};

	
	public void workDone(@NonNull String what);
	
	public void cancel();

	public boolean isCancelled();
	
}
