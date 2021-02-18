/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.markertests;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.Markers;
import com.cevelop.includator.ui.actions.FindUnusedIncludesAction;


public class MarkerTest1OneMarker extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        IFile file = getActiveIncludatorFile().getIFile();
        runAction(new FindUnusedIncludesAction(), AlgorithmScope.FILE_SCOPE);
        IMarker[] markers = file.findMarkers(Markers.INCLUDATOR_UNUSED_INCLUDE_MARKER, false, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 1, (Object) markers.length);
        IMarker marker = markers[0];
        Assert.assertEquals("The include statement '#include \"B.h\"' is unneeded. No reference requires include.", marker.getAttribute(
                IMarker.MESSAGE));
    }
}
