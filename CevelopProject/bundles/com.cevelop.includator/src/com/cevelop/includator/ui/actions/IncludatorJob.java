package com.cevelop.includator.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;

import com.cevelop.includator.IncludatorPlugin;


public abstract class IncludatorJob extends Job {

    protected IWorkbenchWindow window;
    private IStatus            status;

    public IncludatorJob(String name, IWorkbenchWindow window) {
        super(name);
        this.window = window;
        setUser(true);
    }

    /**
     * overriding this run method instead of the default Job.run() causes IncludatorEclipseUIHelper.getActiveWorkbenchWindow() to not return
     * <code>null</code>.
     *
     * @param monitor
     * The {@link IProgressMonitor} to be used
     *
     * @return The {@link IStatus}
     */
    public abstract IStatus runWithWorkbenchWindow(IProgressMonitor monitor);

    @Override
    protected final IStatus run(IProgressMonitor monitor) {
        try {
            IncludatorPlugin.initActiveIncludatorWorkspace();
            IncludatorPlugin.initActiveWorkbenchWindow(window);
            status = runWithWorkbenchWindow(monitor);
            return status;
        } finally {
            IncludatorPlugin.resetActiveWorkbenchWindow();
            IncludatorPlugin.resetActiveIncludatorWorkspace();
        }
    }

    public IStatus getStatus() {
        return status;
    }
}
