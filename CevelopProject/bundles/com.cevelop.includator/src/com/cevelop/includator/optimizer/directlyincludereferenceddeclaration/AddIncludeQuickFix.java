/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.directlyincludereferenceddeclaration;

import org.eclipse.core.resources.IFile;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.text.edits.InsertEdit;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludeInfo;
import com.cevelop.includator.optimizer.IncludatorQuickFix;


public class AddIncludeQuickFix extends IncludatorQuickFix {

    private final IncludeInfo includeToAdd;

    public AddIncludeQuickFix(AddIncludeSuggestion suggestion) {
        super(suggestion);
        includeToAdd = suggestion.getIncludeToAdd();
    }

    @Override
    public String getDescription() {
        return "This action will add an include statement which includes file \"" + includeToAdd.getTargetPath() + "\".";
    }

    @Override
    public String getLabel() {
        return "Add '" + includeToAdd.getIncludeStatementString() + "'.";
    }

    @Override
    public Change getChange(IFile file) {
        int insertOffset = getStartOffset();
        String insertText = includeToAdd.getIncludeStatementString();
        if (getInitialStartOffset() == 0) {
            insertText = insertText + FileHelper.NL;
        } else {
            int adaptedOffset = FileHelper.adaptOffsetToIncludeNextNewline(insertOffset, file);
            if (adaptedOffset != insertOffset) {
                insertOffset = adaptedOffset;
                insertText = insertText + FileHelper.NL;
            } else {
                insertText = FileHelper.NL + insertText;
            }
        }
        return defaultGetChange(file, new InsertEdit(insertOffset, insertText));
    }
}
