package com.cevelop.macronator.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;
import org.junit.Test;

import com.cevelop.macronator.checker.ProblemId;
import com.cevelop.macronator.quickfix.ObjectLikeQuickFix;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingQuickfixTest;


public class ObjectLikeQuickfixTest extends CDTTestingQuickfixTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.OBJECT_LIKE_MACRO;
    }

    @Test
    public void runTest() throws Throwable {
        runQuickfixAndAssertAllEqual();
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new ObjectLikeQuickFix();
    }

}
