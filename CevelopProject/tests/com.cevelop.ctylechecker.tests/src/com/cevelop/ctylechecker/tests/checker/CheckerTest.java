package com.cevelop.ctylechecker.tests.checker;

import org.junit.Test;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingCheckerTest;


public abstract class CheckerTest extends CDTTestingCheckerTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("stdlib");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        assertMarkerLines(expectedMarkerLinesFromProperties);
    }
}
