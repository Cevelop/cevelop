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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.resources.IncludatorProject;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;
import com.cevelop.includator.startingpoints.AlgorithmStartingPointFactory;
import com.cevelop.includator.ui.Markers;


public class RemoveStaticCoverageMarkersAction extends IncludatorAction {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        selection = CUIPlugin.getActivePage().getSelection();
        window = CUIPlugin.getActiveWorkbenchWindow();
        includatorAnalysisJob = new IncludatorJob("Includator Static Coverage Annotations Cleanup Job", window) {

            @Override
            public IStatus runWithWorkbenchWindow(IProgressMonitor monitor) {
                try {
                    AlgorithmStartingPoint startingPoint = AlgorithmStartingPointFactory.getStartingPoint(selection, window);
                    IncludatorProject project = startingPoint.getProject();
                    try {
                        project.getCProject().getProject().deleteMarkers(Markers.COVERAGE_IN_USE_MARKER, true, IResource.DEPTH_INFINITE);
                        project.getCProject().getProject().deleteMarkers(Markers.COVERAGE_IMPLICITLY_IN_USE_MARKER, true, IResource.DEPTH_INFINITE);
                        project.getCProject().getProject().deleteMarkers(Markers.COVERAGE_NOT_IN_USE_MARKER, true, IResource.DEPTH_INFINITE);
                        IncludatorPlugin.getSuggestionStore().cleanOldSuggestions();
                    } catch (CoreException e) {
                        throw new IncludatorException(e);
                    }
                    return Status.OK_STATUS;
                } catch (Exception e) {
                    return new IncludatorStatus("Error while cleaning coverage data", e);
                }
            }
        };
        includatorAnalysisJob.schedule();
        return null;
    }

    @Override
    public void run(IAction action) {
        includatorAnalysisJob = new IncludatorJob("Includator Static Coverage Annotations Cleanup Job", window) {

            @Override
            public IStatus runWithWorkbenchWindow(IProgressMonitor monitor) {
                try {
                    AlgorithmStartingPoint startingPoint = AlgorithmStartingPointFactory.getStartingPoint(selection, window);
                    IncludatorProject project = startingPoint.getProject();
                    try {
                        project.getCProject().getProject().deleteMarkers(Markers.COVERAGE_IN_USE_MARKER, true, IResource.DEPTH_INFINITE);
                        project.getCProject().getProject().deleteMarkers(Markers.COVERAGE_IMPLICITLY_IN_USE_MARKER, true, IResource.DEPTH_INFINITE);
                        project.getCProject().getProject().deleteMarkers(Markers.COVERAGE_NOT_IN_USE_MARKER, true, IResource.DEPTH_INFINITE);
                        IncludatorPlugin.getSuggestionStore().cleanOldSuggestions();
                    } catch (CoreException e) {
                        throw new IncludatorException(e);
                    }
                    return Status.OK_STATUS;
                } catch (Exception e) {
                    return new IncludatorStatus("Error while cleaning coverage data", e);
                }
            }
        };
        includatorAnalysisJob.schedule();
    }

}
