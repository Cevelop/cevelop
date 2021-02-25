package com.cevelop.macronator.tests.refactoring;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.junit.Test;

import com.cevelop.macronator.refactoring.ExpandMacroRefactoring;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingRefactoringTest;


public class ExpandMacroRefactoringTest extends CDTTestingRefactoringTest {

    @Override
    protected Refactoring createRefactoring() {
        return new ExpandMacroRefactoring(getPrimaryCElementFromCurrentProject().get(), getSelectionOfPrimaryTestFile());
    }

    @Test
    public void runTest() throws Throwable {
        openPrimaryTestFileInEditor();
        runRefactoringAndAssertSuccess();
    }
}
