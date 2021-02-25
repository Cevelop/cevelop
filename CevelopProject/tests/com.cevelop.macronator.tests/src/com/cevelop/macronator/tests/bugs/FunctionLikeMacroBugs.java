package com.cevelop.macronator.tests.bugs;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.cevelop.macronator.checker.ProblemId;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingCheckerTest;


public class FunctionLikeMacroBugs extends CDTTestingCheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.FUN_LIKE_MACRO;
    }

    @Test
    public void runTest() throws Throwable {
        assertEquals(0, findMarkers().length);
    }
}
