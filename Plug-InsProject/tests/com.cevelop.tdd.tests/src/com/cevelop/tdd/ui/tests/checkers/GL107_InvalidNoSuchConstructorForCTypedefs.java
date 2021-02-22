package com.cevelop.tdd.ui.tests.checkers;

import org.junit.Test;

import com.cevelop.tdd.helpers.IdHelper.ProblemId;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingCheckerTest;


public class GL107_InvalidNoSuchConstructorForCTypedefs extends CDTTestingCheckerTest {

    protected IProblemId<?> getProblemId() {
        return ProblemId.MISSING_CONSTRUCTOR;
    }

    @Test
    public void runTest() throws Throwable {
        assertMarkerLines(expectedMarkerLinesFromProperties);
    }
}
