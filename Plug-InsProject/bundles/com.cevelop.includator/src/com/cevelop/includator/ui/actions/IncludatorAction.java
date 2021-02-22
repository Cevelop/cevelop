/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


public abstract class IncludatorAction extends AbstractHandler implements IWorkbenchWindowActionDelegate, IObjectActionDelegate {

    public static final String INCLUDATOR_STATUS_NAME = "Includator Static Include Analysis Status";
    protected IWorkbenchWindow window;
    protected IWorkbenchPart   activePart;
    protected ISelection       selection;
    protected IncludatorJob    includatorAnalysisJob;

    @Override
    public void dispose() {}

    @Override
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.activePart = targetPart;
        if ((window == null) && (activePart != null)) {
            window = activePart.getSite().getWorkbenchWindow();
        }
    }

    public IncludatorJob getIncludatorAnalysationJob() {
        return includatorAnalysisJob;
    }
}
