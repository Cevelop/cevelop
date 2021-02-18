/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.startingpoints;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchWindow;


public class IProjectStartingPoint extends CProjectStartingPoint {

    private final IProject iproject;

    public IProjectStartingPoint(IWorkbenchWindow window, IProject iproject) {
        super(window);
        this.iproject = iproject;
    }

    @Override
    protected ICProject getCProject() {
        return CoreModel.getDefault().create(iproject);
    }
}
