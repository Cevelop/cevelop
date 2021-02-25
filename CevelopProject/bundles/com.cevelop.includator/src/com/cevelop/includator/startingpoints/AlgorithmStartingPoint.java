/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.startingpoints;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IWorkbenchWindow;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public abstract class AlgorithmStartingPoint {

    private final IWorkbenchWindow window;

    public AlgorithmStartingPoint(IWorkbenchWindow window) {
        this.window = window;
    }

    public IWorkbenchWindow getActiveWorkbenchWindow() {
        return window;
    }

    public abstract IncludatorProject getProject();

    public abstract IncludatorFile getFile();

    public abstract void clean();

    public abstract List<IncludatorFile> getAffectedFiles();

    public abstract AlgorithmScope getScope();

    public abstract IResource getAffectedResource();

    /**
     * @return the name (not path) of the affected resource as {@link String}.
     */
    public abstract String getAffectedResourceName();
}
