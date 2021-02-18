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
import com.cevelop.includator.ui.actions.RemoveAllIncludatorMarkersAction;


public class MarkerTest4RemoveMarkerAction extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        IFile file = getActiveIncludatorFile().getIFile();

        runAction(new FindUnusedIncludesAction(), AlgorithmScope.FILE_SCOPE);
        IMarker[] markers = file.findMarkers(Markers.INCLUDATOR_UNUSED_INCLUDE_MARKER, false, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 2, (Object) markers.length);

        runAction(new RemoveAllIncludatorMarkersAction(), AlgorithmScope.FILE_SCOPE);
        markers = file.findMarkers(Markers.INCLUDATOR_UNUSED_INCLUDE_MARKER, false, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 0, (Object) markers.length);
    }
}
