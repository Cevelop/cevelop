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

import com.cevelop.includator.helpers.MarkerHelper;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.tests.mocks.CustomSuggestionSelectedAction;
import com.cevelop.includator.ui.Markers;


public class MarkerTest16DirectlyApplyFirstAndAddMarkerSecond extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        CustomSuggestionSelectedAction action = new CustomSuggestionSelectedAction(new OrganizeIncludesAlgorithm());
        action.addExpectedSelection(Suggestion.SOLUTION_OPERATION_APPLY_DEFAULT_CHANGE,
                "The include statement '#include \"A.h\"' is unneeded. No reference requires include.");
        action.addExpectedSelection(Suggestion.SOLUTION_OPERATION_ADD_MARKER, "Missing '#include \"C.h\"'.");
        runAction(action, AlgorithmScope.FILE_SCOPE);
        assertAllSourceFilesEqual(COMPARE_AST_AND_COMMENTS_AND_INCLUDES);

        IFile file = getActiveIncludatorFile().getIFile();
        IMarker[] markers = file.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_ZERO);
        Assert.assertEquals((Object) 1, (Object) markers.length);
        IMarker marker = markers[0];
        Assert.assertEquals((Object) 14, (Object) MarkerHelper.getMarkerStartOffset(marker));
        Assert.assertEquals((Object) 15, (Object) adaptActualOffset(file, MarkerHelper.getMarkerEndOffset(marker)));
    }
}
