/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.ui.actions;

import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.IncludatorStatus;


public class RemoveAllIncludatorMarkersAction extends IncludatorAction {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        selection = CUIPlugin.getActivePage().getSelection();
        window = CUIPlugin.getActiveWorkbenchWindow();
        includatorAnalysisJob = new IncludatorJob("Includator Marker Cleanup Job", window) {

            @Override
            public IStatus runWithWorkbenchWindow(IProgressMonitor monitor) {
                try {
                    IncludatorPlugin.getDefault().clean(monitor);
                    return Status.OK_STATUS;
                } catch (Exception e) {
                    return new IncludatorStatus("Error while cleaning Includator plugin", e);
                }
            }
        };
        includatorAnalysisJob.schedule();
        return null;
    }

    @Override
    public void run(IAction action) {
        includatorAnalysisJob = new IncludatorJob("Includator Marker Cleanup Job", window) {

            @Override
            public IStatus runWithWorkbenchWindow(IProgressMonitor monitor) {
                try {
                    IncludatorPlugin.getDefault().clean(monitor);
                    return Status.OK_STATUS;
                } catch (Exception e) {
                    return new IncludatorStatus("Error while cleaning Includator plugin", e);
                }
            }
        };
        includatorAnalysisJob.schedule();
    }

}
