/*******************************************************************************
 * Copyright (c) 2018, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.ui.tests.extraction;

import java.util.EnumSet;
import java.util.Optional;

import org.eclipse.jface.text.ITextSelection;
import org.junit.Test;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingRefactoringTest;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.comparison.ASTComparison.ComparisonArg;


public abstract class AbstractExtractionTest extends CDTTestingRefactoringTest {

    protected boolean                  overwrite = true;
    protected Optional<ITextSelection> selection;

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("headers/cute");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        openPrimaryTestFileInEditor();
        selection = getSelectionOfPrimaryTestFile();
        runRefactoringAndAssertSuccess();
    }

    @Override
    protected EnumSet<ComparisonArg> makeComparisonArguments() {
        return COMPARE_AST_AND_INCLUDES;
    }
}
