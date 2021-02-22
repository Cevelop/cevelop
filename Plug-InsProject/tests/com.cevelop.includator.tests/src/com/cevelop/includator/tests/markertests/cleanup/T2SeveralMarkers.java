/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.markertests.cleanup;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.Markers;
import com.cevelop.includator.ui.actions.FindUnusedIncludesAction;


public class T2SeveralMarkers extends IncludatorTest {

    @Test
    public void findUnusedIncludesTwice() throws Throwable {
        IFile file = getActiveIncludatorFile().getIFile();
        runAction(new FindUnusedIncludesAction(), AlgorithmScope.FILE_SCOPE);
        IMarker[] firstMarkers = file.findMarkers(Markers.INCLUDATOR_UNUSED_INCLUDE_MARKER, false, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 4, (Object) firstMarkers.length);

        Assert.assertTrue(containsMarkerWithMessage("The include statement '#include \"B.h\"' is unneeded. Duplicate include.", firstMarkers));
        Assert.assertTrue(containsMarkerWithMessage("The include statement '#include \"B.h\"' is unneeded. Covered through: C.h", firstMarkers));
        Assert.assertTrue(containsMarkerWithMessage("The include statement '#include \"D.h\"' is unneeded. No reference requires include.",
                firstMarkers));
        Assert.assertTrue(containsMarkerWithMessage("The include statement '#include \"E.h\"' is unneeded. No reference requires include.",
                firstMarkers));

        runAction(new FindUnusedIncludesAction(), AlgorithmScope.FILE_SCOPE);
        IMarker[] moreMarkers = file.findMarkers(Markers.INCLUDATOR_UNUSED_INCLUDE_MARKER, false, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 4, (Object) moreMarkers.length);

        Assert.assertTrue(containsMarkerWithMessage("The include statement '#include \"B.h\"' is unneeded. Duplicate include.", moreMarkers));
        Assert.assertTrue(containsMarkerWithMessage("The include statement '#include \"B.h\"' is unneeded. Covered through: C.h", moreMarkers));
        Assert.assertTrue(containsMarkerWithMessage("The include statement '#include \"D.h\"' is unneeded. No reference requires include.",
                moreMarkers));
        Assert.assertTrue(containsMarkerWithMessage("The include statement '#include \"E.h\"' is unneeded. No reference requires include.",
                moreMarkers));

        for (IMarker marker : firstMarkers) {
            Assert.assertFalse("Marker " + marker + " still exists", marker.exists());
            Assert.assertFalse(containsMarker(marker, moreMarkers));
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
