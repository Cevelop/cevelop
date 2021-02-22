package com.cevelop.gslator.tests.tests.quickfixes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import org.eclipse.cdt.codan.ui.ICodanMarkerResolution;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IMarkerResolution;
import org.junit.Test;

import com.cevelop.gslator.charwarsstub.quickfixes.gsl.include.ProjectIncluder;
import com.cevelop.gslator.quickfixes.SetAttributeQuickFix;
import com.cevelop.gslator.tests.utils.MarkerHelper;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingQuickfixTest;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.helpers.UIThreadSyncRunnable;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.testsourcefile.TestSourceFile;


public abstract class BaseQuickFixTest extends CDTTestingQuickfixTest {

    protected boolean setIgnoreAttributeQuickFix;
    protected boolean doApplicableCheck;
    protected boolean expectedIsApplicable;
    protected int     markerNr;

    @Override
    protected void configureTest(final Properties properties) {
        setIgnoreAttributeQuickFix = Boolean.parseBoolean(properties.getProperty("setIgnoreAttribute", "false"));
        doApplicableCheck = properties.containsKey("isApplicable");
        expectedIsApplicable = Boolean.parseBoolean(properties.getProperty("isApplicable", "true"));
        markerNr = Integer.parseInt(properties.getProperty("markerNr", "0"));
        super.configureTest(properties);
    }

    @Test
    public void runTest() throws Throwable {
        closeWelcomeScreen();
        IMarker theMarker = null;
        getCurrentProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        final IMarker[] markers = findMarkers();
        assertThatMarkersWereFound(markers);
        if (markerNr > 0) {
            assertTrue("markerNr out of bounds", markerNr <= markers.length);
            List<IMarker> sortedMarkers = MarkerHelper.sortedMarkers(markers);
            theMarker = sortedMarkers.get(markerNr - 1);
        } else {
            final String msg = "Amount of markers not 1 and no markerNr supplied! (use markerNr=1 for first marker)";
            assertEquals(msg, 1, markers.length);
            theMarker = markers[0];
        }

        boolean applicable = true;
        if (doApplicableCheck) {
            applicable = checkApplicable(theMarker, createMarkerResolution(), expectedIsApplicable);
        }
        if (applicable) {
            runQuickFix(theMarker);
        }

        if (ResourcesPlugin.getWorkspace().getRoot().getProject("gsl").exists()) {
            TestSourceFile firstFile = (TestSourceFile) testFiles.values().toArray()[0];
            IFile file = getExpectedCProject().getProject().getFile(firstFile.getName());
            ITranslationUnit unit = CoreModelUtil.findTranslationUnit(file);
            ProjectIncluder.createLinkToProject(unit.getAST());
        }

        assertAllSourceFilesEqual(makeComparisonArguments());
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        if (setIgnoreAttributeQuickFix) {
            return new SetAttributeQuickFix();
        }
        return getQuickFix();
    }

    protected abstract IMarkerResolution getQuickFix();

    protected boolean checkApplicable(final IMarker marker, final IMarkerResolution quickFix, final boolean expected) throws Exception {
        assertEquals("isApplicable can only be tested on ICodanMarkerResolution", true, (quickFix instanceof ICodanMarkerResolution));
        final boolean applicable = checkApplicableOnQuickFix(marker, (ICodanMarkerResolution) quickFix);
        assertEquals("isApplicable does not match expected value: ", expected, applicable);
        return applicable;
    }

    protected boolean checkApplicableOnQuickFix(final IMarker marker, final ICodanMarkerResolution quickFix) throws Exception {
        UIThreadSyncRunnableWithValue runnable = new UIThreadSyncRunnableWithValue() {

            @Override
            protected void runSave() throws Exception {
                value = quickFix.isApplicable(marker);
            }
        };
        runnable.runSyncOnUIThread();
        return runnable.getValue();
    }
}



abstract class UIThreadSyncRunnableWithValue extends UIThreadSyncRunnable {

    boolean value;

    public boolean getValue() {
        return value;
    }
}
