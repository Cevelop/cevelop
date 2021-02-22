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

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IWorkbenchWindow;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public class TranslationUnitStartingPoint extends CProjectStartingPoint {

    private final ITranslationUnit tu;
    private IncludatorFile         file;

    public TranslationUnitStartingPoint(IWorkbenchWindow window, ITranslationUnit tu) {
        super(window);
        this.tu = tu;
    }

    @Override
    protected ICProject getCProject() {
        ICElement projectElement = tu;
        while (!(projectElement instanceof ICProject) && (projectElement != null)) {
            projectElement = projectElement.getParent();
        }
        return (ICProject) projectElement;
    }

    @Override
    public IncludatorFile getFile() {
        if (file == null) {
            IncludatorProject project = getProject();
            if (project != null) {
                IResource tuOrigin = tu.getResource();
                if (tuOrigin instanceof IFile) {
                    file = project.getFile((IFile) tuOrigin);
                } else {
                    file = project.getFile(tu.getLocationURI());
                }
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
        return AlgorithmScope.EDITOR_SCOPE;
    }

    @Override
    public List<IncludatorFile> getAffectedFiles() {
        List<IncludatorFile> result = new ArrayList<>(1);
        result.add(getFile());
        return result;
    }
}
