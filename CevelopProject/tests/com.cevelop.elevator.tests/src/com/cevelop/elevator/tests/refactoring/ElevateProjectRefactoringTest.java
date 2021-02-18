package com.cevelop.elevator.tests.refactoring;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.junit.Test;

import com.cevelop.elevator.refactoring.ElevateProjectRefactoring;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingRefactoringTest;


public class ElevateProjectRefactoringTest extends CDTTestingRefactoringTest {

    @Override
    protected Refactoring createRefactoring() {
        return new ElevateProjectRefactoring(getPrimaryCElementFromCurrentProject().get(), getSelectionOfPrimaryTestFile());
    }

    @Test
    public void runTest() throws Throwable {
        openPrimaryTestFileInEditor();
        runRefactoringAndAssertSuccess();
    }
}
