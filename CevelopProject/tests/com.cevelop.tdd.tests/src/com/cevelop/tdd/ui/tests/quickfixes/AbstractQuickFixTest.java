/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.ui.tests.quickfixes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.junit.Test;

import ch.hsr.ifs.iltis.cpp.core.ui.jface.CTextSelectionUtil;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingQuickfixTest;


public abstract class AbstractQuickFixTest<G extends IMarkerResolutionGenerator2> extends CDTTestingQuickfixTest {

    /**
     * Set this to run only the markers in the selection.
     */
    protected boolean runMarkerInSelection   = false;
    /**
     * Lets the test fail if no selection was found.
     */
    protected boolean failOnMissingSelection = false;

    private IMarkerResolution resolution;
    protected int             markerCount;

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("headers/cute");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        int numberOfMarkers = findMarkers().length;
        assertEquals("Expected and actual marker count do not match.", markerCount, numberOfMarkers);
        if (markerCount > 0) {
            if (runMarkerInSelection) {
                IMarker[] markers = findMarkers();
                Optional<ITextSelection> sel = getSelectionOfPrimaryTestFile();
                if (sel.isPresent()) {
                    boolean foundAMarkerInSel = false;
                    for (IMarker m : markers) {
                        if (CTextSelectionUtil.contains(sel.get(), m)) {
                            runQuickfixAndAssertAllEqual(m);
                            foundAMarkerInSel = true;
                        }
                    }
                    if (!foundAMarkerInSel) {
                        fail("Required to run markers in selection, but no markers were found in the selection.");
                    }
                } else if (failOnMissingSelection) {
                    fail("Required to run marker in selection, but no selection was found in primary testfile.");
                } else {
                    runQuickfixForAllMarkersAndAssertAllEqual();
                }
            } else {
                runQuickfixForAllMarkersAndAssertAllEqual();
            }
            return;
        }
        assertTrue("No checkers enabled, please check your configuration.", checkerEnabled);
        assertTrue("Found markers but expected non.", numberOfMarkers == 0);
    }

    @Override
    protected void runQuickFix(final IMarker marker) {
        assertNotNull("Marker was null. Could not run quick fix for it. ", marker);
        MutableList<IMarkerResolution> resolutions = ArrayIterate.select(getMarkerResolutionGeneratorConstructor().get().getResolutions(marker),
                this::solutionSelectionPredicate);
        assertTrue("No resolutions generated.", resolutions.size() > 0);
        assertTrue("Multiple resolutions found, update the solutionSelectionPredicate", resolutions.size() == 1);
        resolution = resolutions.getFirst();
        super.runQuickFix(marker);
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return resolution;
    }

    protected abstract boolean solutionSelectionPredicate(IMarkerResolution quickfix);

    protected abstract Supplier<G> getMarkerResolutionGeneratorConstructor();

    @Override
    protected void configureTest(Properties properties) {
        markerCount = Integer.parseInt(properties.getProperty("markerCount", "1"));
        super.configureTest(properties);
    }
}
