package com.cevelop.namespactor.tests.checker;

import org.junit.Test;

import com.cevelop.namespactor.helpers.IdHelper.ProblemId;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingCheckerTest;


public class UsingCheckerTest extends CDTTestingCheckerTest {

    @Test
    public void runTest() throws Throwable {
        assertMarkerLines(expectedMarkerLinesFromProperties);
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.UDEC_IN_HEADER_PROBLEM_ID;
    }
}
