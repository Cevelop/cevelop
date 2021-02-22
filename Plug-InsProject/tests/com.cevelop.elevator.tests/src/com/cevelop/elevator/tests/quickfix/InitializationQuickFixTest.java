package com.cevelop.elevator.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;
import org.junit.Test;

import com.cevelop.elevator.ids.IdHelper;
import com.cevelop.elevator.quickfix.InitializationQuickFix;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingQuickfixTest;


public class InitializationQuickFixTest extends QuickFixTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.UNINITIALIZED_VAR;
    }

    @Test
    public void runTest() throws Throwable {
        openPrimaryTestFileInEditor();
        runQuickfixForAllMarkersAndAssertAllEqual();
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new InitializationQuickFix();
    }
}
