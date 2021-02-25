package com.cevelop.ctylechecker.tests.quickfix;

import java.util.stream.Stream;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingQuickfixTest;


public abstract class QuickFixTest extends CDTTestingQuickfixTest {

    @Override
    protected void runQuickfixForAllMarkersAndAssertAllEqual() throws CoreException {
        IMarker[] markers = findMarkers();
        assertThatMarkersWereFound(markers);
        Stream.of(markers).forEach(this::runQuickFix);
        saveAllEditors();
        assertAllSourceFilesEqual(makeComparisonArguments());
    }

    @Test
    public void runTest() throws Throwable {
        runQuickfixForAllMarkersAndAssertAllEqual();
    }
}
