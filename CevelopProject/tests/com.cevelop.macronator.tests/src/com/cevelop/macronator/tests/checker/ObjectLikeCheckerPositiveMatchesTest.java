package com.cevelop.macronator.tests.checker;

import org.junit.Test;

import com.cevelop.macronator.checker.ProblemId;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingCheckerTest;


public class ObjectLikeCheckerPositiveMatchesTest extends CDTTestingCheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.OBJECT_LIKE_MACRO;
    }

    @Test
    public void runTest() throws Throwable {
        assertMarkerLines(expectedMarkerLinesFromProperties);
    }
}
