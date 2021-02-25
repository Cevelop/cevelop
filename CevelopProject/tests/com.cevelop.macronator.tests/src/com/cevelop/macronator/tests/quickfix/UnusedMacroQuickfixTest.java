package com.cevelop.macronator.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;
import org.junit.Test;

import com.cevelop.macronator.checker.ProblemId;
import com.cevelop.macronator.quickfix.UnusedMacroQuickfix;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingQuickfixTest;


public class UnusedMacroQuickfixTest extends CDTTestingQuickfixTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.UNUSED_MACRO;
    }

    @Test
    public void runTest() throws Throwable {
        runQuickfixAndAssertAllEqual();
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new UnusedMacroQuickfix();
    }
}
