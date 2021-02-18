/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer;

import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;


public abstract class IncludatorQuickFix implements IMarkerResolution2 {

    protected Suggestion<?> suggestion;

    public abstract Change getChange(IFile file);

    public IncludatorQuickFix(Suggestion<?> suggestion) {
        this.suggestion = suggestion;
    }

    public int getInitialStartOffset() {
        return suggestion.getInitialStartOffset();
    }

    public int getStartOffset() {
        return suggestion.getStartOffset();
    }

    public int getEndOffset() {
        return suggestion.getEndOffset();
    }

    /**
     * This method should be overridden by QuickFixes which want to react in a special way before applying the change. E.g. show a warning dialog.
     *
     * @return true if the change should be applied, false otherwise
     */
    protected boolean performCustomUserOperation() {
        return true;
    }

    public Change defaultGetChange(IFile file, TextEdit edit) {
        TextFileChange change = new TextFileChange(getLabel(), file);
        change.setSaveMode(TextFileChange.LEAVE_DIRTY);
        change.setEdit(edit);
        return change;
    }

    @Override
    public String toString() {
        return getLabel() + "[" + getStartOffset() + "," + getEndOffset() + "]";
    }

    @Override
    public void run(IMarker marker) {
        boolean wasWorkbenchAlreadySet = true;
        try {
            IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            wasWorkbenchAlreadySet = IncludatorPlugin.initActiveWorkbenchWindow(activeWorkbenchWindow);
            IFile file = suggestion.getIFile();
            try {
                IDE.openEditor(IncludatorPlugin.getActiveWorkbenchWindow().getActivePage(), file);
            } catch (PartInitException e) {
                throw new IncludatorException(e);
            }
            runOnFile(file);
        } catch (CoreException e) {
            throw new IncludatorException("Unable to apply suggestion.", e);
        } finally {
            if (!wasWorkbenchAlreadySet) {
                FileHelper.clean();
            }
        }
    }

    private void runOnFile(IFile file) throws CoreException {
        if (performCustomUserOperation()) {
            getChange(file).perform(new NullProgressMonitor());
            IncludatorPlugin.getSuggestionStore().removeSuggestion(suggestion);
        }
    }

    @Override
    public Image getImage() {
        return CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_CORRECTION_CHANGE);
    }
}
