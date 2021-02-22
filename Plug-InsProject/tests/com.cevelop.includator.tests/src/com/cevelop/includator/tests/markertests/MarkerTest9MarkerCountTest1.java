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
import com.cevelop.includator.ui.actions.OrganizeIncludesAction;


public class MarkerTest9MarkerCountTest1 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {

        runAction(new OrganizeIncludesAction(), AlgorithmScope.FILE_SCOPE);

        IFile file = getActiveIncludatorFile().getIFile();
        IMarker[] addIncludeMarker = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 1, (Object) addIncludeMarker.length);
        applyMarker(addIncludeMarker[0]);

        IMarker[] noMarkers = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 0, (Object) noMarkers.length);
    }
}
