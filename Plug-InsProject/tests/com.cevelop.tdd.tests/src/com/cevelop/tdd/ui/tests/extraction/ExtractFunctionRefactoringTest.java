/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.ui.tests.extraction;

import org.eclipse.cdt.core.model.ICElement;

import com.cevelop.tdd.ui.tests.extraction.mock.MockExtractRefactoring;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;


public class ExtractFunctionRefactoringTest extends AbstractExtractionTest {

    @Override
    protected CRefactoring createRefactoring() {
        ICElement elem = getPrimaryCElementFromCurrentProject().get();
        MockExtractRefactoring refactoring = new MockExtractRefactoring(elem, selection, overwrite);
        return refactoring;
    }
}
