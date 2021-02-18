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

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IWorkbenchWindow;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public class CProjectStartingPoint extends AlgorithmStartingPoint {

    private IncludatorProject project;
    private ICProject         cproject;

    public CProjectStartingPoint(IWorkbenchWindow window) {
        super(window);
    }

    @Override
    public IncludatorProject getProject() {
        ICProject cProject = getCProject();
        if (cProject != null) {
            project = IncludatorPlugin.getWorkspace().getProject(cProject);
        }
        return project;
    }

    protected ICProject getCProject() {
        return cproject;
    }

    @Override
    public IncludatorFile getFile() {
        return null;
    }

    public void setCProject(ICProject cproject) {
        this.cproject = cproject;
    }

    @Override
    public void clean() {
        project = null;
    }

    @Override
    public List<IncludatorFile> getAffectedFiles() {
        return getProject().getAffectedFiles();
    }

    @Override
    public AlgorithmScope getScope() {
        return AlgorithmScope.PROJECT_SCOPE;
    }

    @Override
    public IResource getAffectedResource() {
        return project.getCProject().getProject();
    }

    @Override
    public String getAffectedResourceName() {
        return getAffectedResource().getName();
    }
}
