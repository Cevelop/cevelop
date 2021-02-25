/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.ltk.core.refactoring.Change;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.preferences.IncludatorPropertyManager;


public class SuppressSuggestionQuickFix extends IncludatorQuickFix {

    private final String fileNameToIgnore;

    public SuppressSuggestionQuickFix(Suggestion<?> suggestion) {
        super(suggestion);
        fileNameToIgnore = suggestion.getSuppressSuggestionTargetFileName();
    }

    @Override
    public String getDescription() {
        return "This action will configure Includator to not propose any suggestion of the include under consideration in the current file.";
    }

    @Override
    public String getLabel() {
        return "Suppress in future.";
    }

    @Override
    public void run(IMarker marker) {
        String filePath = suggestion.getProjectRelativePath();
        IFile iFile = suggestion.getIFile();
        IProject project = iFile.getProject();
        IncludatorPropertyManager.addIgnoredInclude(project, filePath, fileNameToIgnore);
        IncludatorPlugin.getSuggestionStore().removeSuggestion(suggestion);
    }

    @Override
    public Change getChange(IFile file) {
        return null;
    }
}
