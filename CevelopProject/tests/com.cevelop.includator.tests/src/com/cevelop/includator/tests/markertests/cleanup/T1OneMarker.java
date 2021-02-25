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
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.Markers;
import com.cevelop.includator.ui.actions.FindUnusedIncludesAction;
import com.cevelop.includator.ui.actions.IncludatorAction;
import com.cevelop.includator.ui.actions.OrganizeIncludesAction;


public class T1OneMarker extends IncludatorTest {

    @Test
    public void findUnusedIncludesTwice() throws Throwable {
        invokeTwoActions(new FindUnusedIncludesAction(), new FindUnusedIncludesAction());
    }

    @Test
    public void superAlgorithmAndDerivedAlgorithm() throws Throwable {
        invokeTwoActions(new FindUnusedIncludesAction(), new OrganizeIncludesAction());
    }

    @Test
    public void derivedAlgorithmAndSuperAlgorithm() throws Throwable {
        invokeTwoActions(new OrganizeIncludesAction(), new FindUnusedIncludesAction());
    }

    private void invokeTwoActions(IncludatorAction firstAction, IncludatorAction secondAction) throws Exception {
        IFile file = getActiveIncludatorFile().getIFile();
        runAction(firstAction, AlgorithmScope.FILE_SCOPE);
        IMarker[] firstMarkers = file.findMarkers(Markers.INCLUDATOR_UNUSED_INCLUDE_MARKER, false, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 1, (Object) firstMarkers.length);
        IMarker firstMarker = firstMarkers[0];
        Assert.assertEquals("The include statement '#include \"B.h\"' is unneeded. No reference requires include.", firstMarker.getAttribute(
                IMarker.MESSAGE));

        runAction(secondAction, AlgorithmScope.FILE_SCOPE);
        IMarker[] moreMarkers = file.findMarkers(Markers.INCLUDATOR_UNUSED_INCLUDE_MARKER, false, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 1, (Object) moreMarkers.length);
        IMarker newMarker = moreMarkers[0];
        Assert.assertEquals("The include statement '#include \"B.h\"' is unneeded. No reference requires include.", newMarker.getAttribute(
                IMarker.MESSAGE));

        Assert.assertFalse(firstMarker.exists());
        Assert.assertFalse(firstMarker.equals(newMarker));
    }

}
