/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.markertests.cleanup;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.Markers;
import com.cevelop.includator.ui.actions.FindUnusedIncludesAction;


public class T4MixedScopeMarker extends IncludatorTest {

    @Test
    public void findUnusedIncludesInProjectAndLocally() throws Throwable {
        final IMarker[] firstMarkers = runFindUnusedIncludesInProject();

        runAction(new FindUnusedIncludesAction(), AlgorithmScope.FILE_SCOPE);
        IMarker[] moreMarkers = getCurrentProject().findMarkers(Markers.INCLUDATOR_UNUSED_INCLUDE_MARKER, false, IResource.DEPTH_INFINITE);
        Assert.assertEquals((Object) 2, (Object) moreMarkers.length);

        Assert.assertTrue(containsMarkerWithMessage("The include statement '#include \"B.h\"' is unneeded. No reference requires include.",
                moreMarkers));
        Assert.assertTrue(containsMarkerWithMessage("The include statement '#include \"C.h\"' is unneeded. No reference requires include.",
                moreMarkers));

        for (IMarker marker : moreMarkers) {
            if (marker.getAttribute(IMarker.MESSAGE).toString().contains("'#include \"B.h\"'")) {
                Assert.assertFalse(containsMarker(marker, firstMarkers));
            } else {
                Assert.assertTrue("Marker " + marker + " does not exist anymore", marker.exists());
                Assert.assertTrue(containsMarker(marker, firstMarkers));
            }
        }
    }

    private IMarker[] runFindUnusedIncludesInProject() throws Exception {
        runAction(new FindUnusedIncludesAction(), AlgorithmScope.PROJECT_SCOPE);
        IMarker[] markers = getCurrentProject().findMarkers(Markers.INCLUDATOR_UNUSED_INCLUDE_MARKER, false, IResource.DEPTH_INFINITE);

        Assert.assertEquals((Object) 2, (Object) markers.length);
        Assert.assertTrue(containsMarkerWithMessage("The include statement '#include \"B.h\"' is unneeded. No reference requires include.", markers));
        Assert.assertTrue(containsMarkerWithMessage("The include statement '#include \"C.h\"' is unneeded. No reference requires include.", markers));
        return markers;
    }

    @Test
    public void findUnusedIncludesInProjectTwice() throws Throwable {
        IMarker[] firstMarkers = runFindUnusedIncludesInProject();

        IMarker[] moreMarkers = runFindUnusedIncludesInProject();
        for (IMarker marker : moreMarkers) {
            Assert.assertFalse(containsMarker(marker, firstMarkers));
        }
    }

    private boolean containsMarkerWithMessage(String markerMessage, IMarker[] markers) throws CoreException {
        for (IMarker marker : markers) {
            if (markerMessage.equals(marker.getAttribute(IMarker.MESSAGE))) return true;
        }
        return false;
    }

    private boolean containsMarker(IMarker markerToSeek, IMarker[] markers) throws CoreException {
        for (IMarker marker : markers) {
            if (marker.equals(markerToSeek)) return true;
        }
        return false;
    }

}
