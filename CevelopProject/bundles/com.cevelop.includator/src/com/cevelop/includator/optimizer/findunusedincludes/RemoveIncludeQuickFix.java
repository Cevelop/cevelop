/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.findunusedincludes;

import org.eclipse.core.resources.IFile;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.text.edits.DeleteEdit;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.optimizer.IncludatorQuickFix;
import com.cevelop.includator.optimizer.Suggestion;


public class RemoveIncludeQuickFix extends IncludatorQuickFix {

    private int length;
    private int endOffset;

    public RemoveIncludeQuickFix(Suggestion<?> suggestion) {
        super(suggestion);
    }

    @Override
    public String getDescription() {
        return "This action will remove the include from the current document.";
    }

    @Override
    public String getLabel() {
        return "Delete unused include.";
    }

    @Override
    public Change getChange(IFile file) {
        endOffset = getEndOffset();
        endOffset = FileHelper.adaptOffsetToIncludeNextNewline(endOffset, file);
        int startOffset = getStartOffset();
        length = endOffset - startOffset;
        return defaultGetChange(file, new DeleteEdit(startOffset, length));
    }
}
