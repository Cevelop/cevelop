/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.markertests;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.tests.base.DeprecatedUndoTest;
import com.cevelop.includator.ui.Markers;
import com.cevelop.includator.ui.actions.OrganizeIncludesAction;


public class MarkerTest7UndoMarkerTest2WithUserTyping extends DeprecatedUndoTest {

    @Test
    public void runTest() throws Throwable {
        IFile file = getActiveIncludatorFile().getIFile();
        openPrimaryTestFileInEditor();

        runAction(new OrganizeIncludesAction(), AlgorithmScope.EDITOR_SCOPE);

        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();
        Assert.assertEquals((Object) 1, (Object) suggestions.size());

        insertTextIntoActiveDocument("//hello" + NL, 0);

        applySuggestion(suggestions.get(0));
        assertAllSourceFilesEqual(COMPARE_AST_AND_COMMENTS_AND_INCLUDES);

        insertTextIntoActiveDocument("//hello" + NL, 0);

        IMarker[] undoMarkers = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 0, (Object) undoMarkers.length);

        applyAddIncludeUndoMarker(31, 14 + NL.length());

        String expected = "//hello" + NL + "//hello" + NL + testFiles.get(getNameOfPrimaryTestFile()).getSource();
        Assert.assertEquals(expected, getCurrentDocument(getCurrentIFile(getNameOfPrimaryTestFile()).getLocationURI()).get());
        IMarker[] remainingMarkers = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 0, (Object) remainingMarkers.length);

        closeOpenEditors();
        FileHelper.clean();
    }
}
