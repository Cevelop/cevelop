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

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.tests.base.DeprecatedUndoTest;
import com.cevelop.includator.ui.Markers;
import com.cevelop.includator.ui.actions.OrganizeIncludesAction;


public class MarkerTest10MarkerCountTest2 extends DeprecatedUndoTest {

    @Test
    public void runTest() throws Throwable {
        IFile file = getActiveIncludatorFile().getIFile();
        openPrimaryTestFileInEditor();

        runAction(new OrganizeIncludesAction(), AlgorithmScope.EDITOR_SCOPE);

        IMarker[] addIncludeMarker = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 1, (Object) addIncludeMarker.length);

        // applying the suggestion through the ApplySuggestionRunnable will cause an undo marker to be added. (this is not true anymore)
        applySuggestion(IncludatorPlugin.getSuggestionStore().findSuggestion(addIncludeMarker[0]));

        IMarker[] undoMarker = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 0, (Object) undoMarker.length);
        applyAddIncludeUndoMarker(15, 15);

        IMarker[] noMarkers = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 0, (Object) noMarkers.length);
        closeOpenEditors();
    }
}
