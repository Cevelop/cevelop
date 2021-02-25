package com.cevelop.gslator.tests.tests.checkers;

import org.junit.Test;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingCheckerTest;


public abstract class BaseCheckerTest extends CDTTestingCheckerTest {

    @Test
    public void runTest() throws Throwable {
        assertMarkerLines(expectedMarkerLinesFromProperties);
    }
}
