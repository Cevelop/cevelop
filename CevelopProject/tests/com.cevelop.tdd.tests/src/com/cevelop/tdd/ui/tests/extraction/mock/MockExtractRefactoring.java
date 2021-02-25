/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.ui.tests.extraction.mock;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import com.cevelop.tdd.infos.ExtractToHeaderInfo;
import com.cevelop.tdd.refactorings.extract.ExtractToHeaderRefactoring;


public class MockExtractRefactoring extends ExtractToHeaderRefactoring {

    private boolean shouldOverwrite;

    public MockExtractRefactoring(ICElement icElement, Optional<ITextSelection> selection, boolean overwrite) {
        super(icElement, new ExtractToHeaderInfo().also(c -> c.setSelection(selection)));
        this.shouldOverwrite = overwrite;
    }

    @Override
    protected boolean shouldOverwriteOnUserRequest(String name) {
        return shouldOverwrite;
    }
}
