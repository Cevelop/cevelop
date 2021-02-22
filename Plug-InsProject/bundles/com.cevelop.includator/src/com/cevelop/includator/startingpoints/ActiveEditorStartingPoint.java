/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.startingpoints;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.internal.ui.util.ExternalEditorInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.FileEditorInput;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;
import com.cevelop.includator.ui.helpers.IncludatorEclipseUIHelper;


@SuppressWarnings("restriction")
public class ActiveEditorStartingPoint extends AlgorithmStartingPoint {

    private IncludatorFile    file;
    private IncludatorProject project;
    private IFile             iFile;

    public ActiveEditorStartingPoint(IWorkbenchWindow window) {
        super(window);
        initActveEditorIncludatorFile(getActiveWorkbenchWindow());
    }

    private void initActveEditorIncludatorFile(IWorkbenchWindow window) {
        try {
            IEditorInput editorInput = IncludatorEclipseUIHelper.getActiveEditorInput(window);
            if (editorInput == null) {
                throw new IncludatorException("Unable to find the active file. No active C/C++ editor found.");
            }

            if (editorInput instanceof FileEditorInput) {
                file = getFileFromNormalFileEditor((FileEditorInput) editorInput);
            } else if (editorInput instanceof ExternalEditorInput) {
                file = getFileFromExternalFileEdior((ExternalEditorInput) editorInput);
            } else {
                throw new IncludatorException("Could not launch static analysis because the editor input is not a file-editor-input");
            }
        } catch (Exception e) {
            throw new IncludatorException("Unable to find the active file", e);
        }
    }

    private IncludatorFile getFileFromExternalFileEdior(ExternalEditorInput externalEditorInput) {
        URI uri = externalEditorInput.getURI();
        initIncludatorProject((IProject) externalEditorInput.getMarkerResource());
        return project.getFile(uri);
    }

    private IncludatorFile getFileFromNormalFileEditor(FileEditorInput fileEditorInput) {
        iFile = fileEditorInput.getFile();
        initIncludatorProject(iFile.getProject());
        return project.getFile(iFile);
    }

    private void initIncludatorProject(IProject project) {
        ICProject cProject = CoreModel.getDefault().create(project);
        this.project = IncludatorPlugin.getWorkspace().getProject(cProject);
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
    }

    @Override
    public AlgorithmScope getScope() {
        return AlgorithmScope.EDITOR_SCOPE;
    }

    @Override
    public IResource getAffectedResource() {
        return iFile != null ? iFile : file.getIFile();
    }

    @Override
    public List<IncludatorFile> getAffectedFiles() {
        final List<IncludatorFile> result = new ArrayList<>();
        result.add(file);
        return result;
    }

    @Override
    public String getAffectedResourceName() {
        if (file != null) {
            return FileHelper.stringToPath(file.getFilePath()).lastSegment();
        } else if (iFile != null) {
            return iFile.getName();
        } else {
            return "unknown_file_name";
        }
    }
}
