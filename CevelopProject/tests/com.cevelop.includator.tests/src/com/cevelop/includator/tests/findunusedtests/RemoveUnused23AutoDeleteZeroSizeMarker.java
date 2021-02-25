/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.findunusedtests;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.Markers;
import com.cevelop.includator.ui.actions.FindUnusedIncludesAction;


public class RemoveUnused23AutoDeleteZeroSizeMarker extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        openPrimaryTestFileInEditor();
        runAction(new FindUnusedIncludesAction(), AlgorithmScope.EDITOR_SCOPE);

        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();
        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        Assert.assertEquals((Object) 1, (Object) suggestions.size());

        Suggestion<?> removeIncludeSuggestion = suggestions.get(0);
        assertSuggestion(removeIncludeSuggestion, "main.cpp", "The include statement '#include \"A.h\"' is unneeded. No reference requires include.",
                0, 14);

        assertMarkerInActiveDocument(removeIncludeSuggestion, true);
        deleteFromActiveDocument(0, 14);
        assertMarkerInActiveDocument(removeIncludeSuggestion, true);
        saveAllEditors();
        assertMarkerInActiveDocument(removeIncludeSuggestion, false);
    }

    private void assertMarkerInActiveDocument(Suggestion<?> suggestion, boolean exists) throws CoreException {
        IFile file = getActiveIncludatorFile().getIFile();
        IMarker[] markers = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) (exists ? 1 : 0), (Object) markers.length);
        if (exists) {
            Assert.assertEquals(markers[0], suggestion.getMarker());
        }
        Assert.assertEquals(exists, suggestion.getMarker().exists());
    }
}
