/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.startingpoints;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IWorkbenchWindow;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public class FileStartingPoint extends AlgorithmStartingPoint {

    private IncludatorFile    file;
    private IncludatorProject project;

    public FileStartingPoint(IWorkbenchWindow window, IncludatorFile file) {
        super(window);
        this.file = file;
        this.project = file.getProject();
    }

    @Override
    public IncludatorProject getProject() {
        return project;
    }

    @Override
    public IncludatorFile getFile() {
        return file;
    }

    @Override
    public void clean() {
        file = null;
        project = null;
    }

    @Override
    public List<IncludatorFile> getAffectedFiles() {
        List<IncludatorFile> result = new ArrayList<>(1);
        result.add(file);
        return result;
    }

    @Override
    public AlgorithmScope getScope() {
        return AlgorithmScope.FILE_SCOPE;
    }

    @Override
    public IResource getAffectedResource() {
        return file.getIFile();
    }

    @Override
    public String getAffectedResourceName() {
        return FileHelper.stringToPath(file.getFilePath()).lastSegment();
    }
}
