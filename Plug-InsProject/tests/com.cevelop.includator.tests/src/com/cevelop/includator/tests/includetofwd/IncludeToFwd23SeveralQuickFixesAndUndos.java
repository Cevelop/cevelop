/*******************************************************************************
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


public class IncludeToFwd23SeveralQuickFixesAndUndos extends DeprecatedUndoTest {

    @Test
    public void runTest() throws Throwable {
        IFile file = getActiveIncludatorFile().getIFile();
        openPrimaryTestFileInEditor();;

        runAction(new ReplaceIncludesWithFwdAction(), AlgorithmScope.EDITOR_SCOPE);

        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();
        Assert.assertEquals((Object) 2, (Object) suggestions.size());

        Suggestion<?> fwdASuggestion = suggestions.get(0);
        Suggestion<?> fwdBSuggestion = suggestions.get(1);
        applySuggestion(fwdASuggestion);
        applySuggestion(fwdBSuggestion);
        assertAllSourceFilesEqual(COMPARE_AST_AND_COMMENTS_AND_INCLUDES);

        IMarker[] undoMarkers = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 0, (Object) undoMarkers.length);

        applyFwdUndoMarker(0, "#include \"A.h\"", 0, 8 + NL.length());
        applyFwdUndoMarker(15, "#include \"B.h\"", 15, 8 + NL.length());

        Assert.assertEquals(testFiles.get(getNameOfPrimaryTestFile()), getCurrentDocument(getCurrentIFile(getNameOfPrimaryTestFile())
                .getLocationURI()).get());
        IMarker[] remainingMarkers = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 0, (Object) remainingMarkers.length);
        closeOpenEditors();
    }
}
