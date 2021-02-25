/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.findunusedfiles;

import org.eclipse.cdt.internal.ui.refactoring.changes.DeleteFileChange;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.swt.widgets.Shell;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.IncludatorQuickFix;
import com.cevelop.includator.optimizer.Suggestion;


@SuppressWarnings("restriction")
public class RemoveFileQuickFix extends IncludatorQuickFix {

    @Override
    protected boolean performCustomUserOperation() {
        Shell shell = IncludatorPlugin.getActiveWorkbenchWindow().getShell();
        return MessageDialog.openConfirm(shell, "Confirm deletion", "Deleting the file \"" + suggestion.getProjectRelativePath() +
                                                                    "\" cannot be undone. Continue?");
    }

    public RemoveFileQuickFix(Suggestion<?> suggestion) {
        super(suggestion);
    }

    @Override
    public String getDescription() {
        return "Removes the file " + suggestion.getProjectRelativePath() + " from the current project.";
    }

    @Override
    public String getLabel() {
        return "Remove file";
    }

    @Override
    public Change getChange(IFile file) {
        IFile ifile = suggestion.getIFile();
        return new DeleteFileChange(ifile.getFullPath());
    }
}
