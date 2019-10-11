package anatlyzer.testing.ui;

import anatlyzer.testing.common.IProgressMonitor;

public class EclipseTestProgressMonitor implements IProgressMonitor {

	private org.eclipse.core.runtime.IProgressMonitor monitor;
	
	public EclipseTestProgressMonitor(org.eclipse.core.runtime.IProgressMonitor monitor) {
		this.monitor = monitor;
	}
	
	@Override
	public void beginWork(String what, int size) {
		monitor.beginTask(what, size);
	}

	@Override
	public void workDone(String what, int size) {
		monitor.worked(size);
	}

	@Override
	public void cancel() {
		monitor.setCanceled(true);
	}

	@Override
	public boolean isCancelled() {
		return monitor.isCanceled();
	}

}
