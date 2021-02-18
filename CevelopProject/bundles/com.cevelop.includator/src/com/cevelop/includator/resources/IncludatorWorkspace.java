/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.resources;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.cevelop.includator.helpers.IncludatorException;


public class IncludatorWorkspace {

    private final IWorkspace                        eclipseWorkspace;
    private final Map<ICProject, IncludatorProject> projects;

    public IncludatorWorkspace(IWorkspace eclipseWorkspace) {
        this.eclipseWorkspace = eclipseWorkspace;
        projects = new LinkedHashMap<>();
    }

    public IWorkspace getEclipseWorkspace() {
        return eclipseWorkspace;
    }

    public IncludatorProject getProject(ICProject cproject) {
        loadProject(cproject);
        return projects.get(cproject);
    }

    private void loadProject(ICProject cproject) {
        if (!projects.containsKey(cproject)) {
            projects.put(cproject, new IncludatorProject(cproject));
        }
    }

    public void clear() {
        for (IncludatorProject curProject : projects.values()) {
            curProject.clear();
        }
        projects.clear();
    }

    public void purge() {
        for (IncludatorProject curProject : projects.values()) {
            curProject.purge();
        }
    }

    public Collection<IncludatorProject> getAllProjects() {
        for (IProject curProj : eclipseWorkspace.getRoot().getProjects()) {
            try {
                if (curProj.isOpen() && curProj.hasNature("org.eclipse.cdt.core.cnature")) {
                    ICProject cproject = CoreModel.getDefault().create(curProj.getLocation()).getCProject();
                    loadProject(cproject);
                }
            } catch (CoreException e) {
                throw new IncludatorException(e);
            }
        }
        return projects.values();
    }

    /**
     * Returns a IncludatorProject for a given absolute path. the path can also be a sub-resource of the element.
     *
     * @param absolutePathToContainedElement The absolute path to the containing element as {@link String}
     * @return The {@link IncludatorProject}
     */
    public IncludatorProject findProject(String absolutePathToContainedElement) {
        return getProject(CoreModel.getDefault().create(new Path(absolutePathToContainedElement)).getCProject());
    }
}
