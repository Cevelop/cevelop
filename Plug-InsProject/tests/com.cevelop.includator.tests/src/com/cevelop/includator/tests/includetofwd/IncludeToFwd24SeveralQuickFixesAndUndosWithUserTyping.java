/*******************************************************************************
 *
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.includetofwd;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.tests.base.DeprecatedUndoTest;
import com.cevelop.includator.ui.Markers;
import com.cevelop.includator.ui.actions.ReplaceIncludesWithFwdAction;


public class IncludeToFwd24SeveralQuickFixesAndUndosWithUserTyping extends DeprecatedUndoTest {

    @Test
    public void runTest() throws Throwable {
        IFile file = getActiveIncludatorFile().getIFile();
        String expected1FileName = "expected.cpp";
        String expected2FileName = "expected2.cpp";
        openPrimaryTestFileInEditor();
        runAction(new ReplaceIncludesWithFwdAction(), AlgorithmScope.EDITOR_SCOPE);
        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();

        Assert.assertEquals((Object) 2, (Object) suggestions.size());
        Suggestion<?> fwdASuggestion = suggestions.get(0);
        Suggestion<?> fwdBSuggestion = suggestions.get(1);

        applySuggestion(fwdASuggestion);
        insertTextIntoActiveDocument(NL + "//blub3", 31);
        insertTextIntoActiveDocument(NL + "//blub2", 22);
        assertAllSourceFilesEqual(COMPARE_AST_AND_COMMENTS_AND_INCLUDES);

        applySuggestion(fwdBSuggestion);
        insertTextIntoActiveDocument(NL + "//blub44", 41);
        insertTextIntoActiveDocument("3", 32);
        insertTextIntoActiveDocument("2", 15);
        insertTextIntoActiveDocument("1", 7);
        insertTextIntoActiveDocument("//blub00" + NL, 0);

        Assert.assertEquals(testFiles.get(expected1FileName), getCurrentDocument(getCurrentIFile(getNameOfPrimaryTestFile()).getLocationURI()).get());

        IMarker[] undoMarkers = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 0, (Object) undoMarkers.length);

        applyFwdUndoMarker(9, "#include \"A.h\"", 27, 16 + 2 * NL.length());
        insertTextIntoActiveDocument("4", 59);
        insertTextIntoActiveDocument("2", 41);
        insertTextIntoActiveDocument("1", 32);
        insertTextIntoActiveDocument("0", 8);
        Assert.assertEquals(getExpectedDocument(getExpectedIFile(expected1FileName)).get(), getCurrentDocument(getCurrentIFile(
                getNameOfPrimaryTestFile())).get());

        applyFwdUndoMarker(35, "#include \"B.h\"", 45, 17 + 2 * NL.length());

        Assert.assertEquals(testFiles.get(expected2FileName), getCurrentDocument(getCurrentIFile(getNameOfPrimaryTestFile()).getLocationURI()).get());

        IMarker[] remainingMarkers = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 0, (Object) remainingMarkers.length);

        closeOpenEditors();
    }
}
