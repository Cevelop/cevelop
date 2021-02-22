package com.cevelop.intwidthfixator.tests.checker;

import org.junit.Test;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingCheckerTest;


public abstract class AbstractCheckerTest extends CDTTestingCheckerTest {

    @Test
    public void runTest() throws Throwable {
        assertMarkerLines(expectedMarkerLinesFromProperties);
    }
}
