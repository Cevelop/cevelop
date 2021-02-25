/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.includetofwd;

import java.net.URI;
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


public class IncludeToFwd21QuickFixAndUndo extends DeprecatedUndoTest {

    @Test
    public void runTest() throws Throwable {
        runAction(new ReplaceIncludesWithFwdAction(), AlgorithmScope.FILE_SCOPE);

        URI absoluteFileName = currentProjectHolder.makeProjectAbsoluteURI(getNameOfPrimaryTestFile());
        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().findSuggestions(absoluteFileName, 0);
        Assert.assertEquals((Object) 1, (Object) suggestions.size());

        applySuggestion(suggestions.get(0));
        assertAllSourceFilesEqual(COMPARE_AST_AND_COMMENTS_AND_INCLUDES);

        IFile file = getActiveIncludatorFile().getIFile();
        IMarker[] undoMarkers = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 0, (Object) undoMarkers.length);

        applyFwdUndoMarker(0, "#include \"A.h\"", 0, 8 + NL.length());

        IMarker[] remainingMarkers = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 0, (Object) remainingMarkers.length);
    }
}
