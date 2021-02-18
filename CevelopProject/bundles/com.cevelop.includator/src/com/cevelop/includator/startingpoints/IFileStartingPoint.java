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

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IWorkbenchWindow;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public class IFileStartingPoint extends CProjectStartingPoint {

    private final IFile    ifile;
    private IncludatorFile file;

    public IFileStartingPoint(IWorkbenchWindow window, IFile ifile) {
        super(window);
        this.ifile = ifile;
    }

    @Override
    protected ICProject getCProject() {
        return CoreModel.getDefault().create(ifile.getProject());
    }

    @Override
    public IncludatorFile getFile() {
        if (file == null) {
            IncludatorProject project = getProject();
            if (project != null) {
                file = project.getFile(ifile);
            }
        }
        return file;
    }

    @Override
    public void clean() {
        super.clean();
        file = null;
    }

    @Override
    public AlgorithmScope getScope() {
        return AlgorithmScope.FILE_SCOPE;
    }

    @Override
    public List<IncludatorFile> getAffectedFiles() {
        List<IncludatorFile> result = new ArrayList<>(1);
        result.add(file);
        return result;
    }

    @Override
    public IResource getAffectedResource() {
        return ifile;
    }
}
