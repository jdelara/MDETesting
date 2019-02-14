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
		public void beginWork(String what, int size) { }
		
		@Override
		public void workDone(@NonNull String what, int size) { }

		@Override
		public void cancel() {
			this.cancel = true;
		}

		@Override
		public boolean isCancelled() {
			return cancel;
		}		
	};

	public static class Empty implements IProgressMonitor {

		@Override
		public void workDone(@NonNull String what, int size) { }

		@Override
		public void cancel() {	}

		@Override
		public boolean isCancelled() { return false; }

		@Override
		public void beginWork(String what, int size) { }
		
	}

	public void beginWork(String what, int size);
	
	public void workDone(@NonNull String what, int size);
	
	public void cancel();

	public boolean isCancelled();

	
}
