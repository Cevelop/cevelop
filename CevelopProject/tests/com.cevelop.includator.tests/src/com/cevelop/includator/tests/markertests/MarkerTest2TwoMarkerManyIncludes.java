/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.markertests;

import static org.junit.Assert.fail;

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


public class MarkerTest2TwoMarkerManyIncludes extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        runAction(new FindUnusedIncludesAction(), AlgorithmScope.FILE_SCOPE);

        IFile file = getActiveIncludatorFile().getIFile();
        IMarker[] markers = file.findMarkers(Markers.INCLUDATOR_UNUSED_INCLUDE_MARKER, false, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 2, (Object) markers.length);
        assertIncludeMarker(markers, "#include \"B.h\"");
        assertIncludeMarker(markers, "#include \"E.h\"");
    }

    void assertIncludeMarker(final IMarker[] markers, final String expectedInclude) throws CoreException {
        final String expected = "The include statement '" + expectedInclude + "' is unneeded. No reference requires include.";
        for (final IMarker curMarker : markers) {
            if (curMarker.getAttribute(IMarker.MESSAGE).equals(expected)) {
                return;
            }
        }
        fail("markers collection did not contain a marker for include statement " + expectedInclude);
    }
}
