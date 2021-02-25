package com.cevelop.templator.tests.asttests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base.CDTTestingUITest;


public class SourceFileContentTest extends CDTTestingUITest {

    @Test
    public void runTest() throws Throwable {
        assertEquals("XY.cpp", getNameOfPrimaryTestFile());
        assertEquals("#include <iostream>" + NL + NL + "int main() {}", testFiles.get(getNameOfPrimaryTestFile()).getSource());
        assertEquals("int main() {}", testFiles.get(getNameOfPrimaryTestFile()).getExpectedSource());
    }
}
