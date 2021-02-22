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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.Markers;
import com.cevelop.includator.ui.actions.OrganizeIncludesAction;
import com.cevelop.includator.ui.actions.RemoveStaticCoverageMarkersAction;
import com.cevelop.includator.ui.actions.StaticCoverageAnalysisAction;


public class MarkerTest6PartOfMarkersDeleted extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        runAction(new OrganizeIncludesAction(), AlgorithmScope.FILE_SCOPE);
        runAction(new StaticCoverageAnalysisAction(), AlgorithmScope.FILE_SCOPE);
        List<Suggestion<?>> allSuggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();
        Assert.assertEquals((Object) 6, (Object) allSuggestions.size());
        IMarker[] markers = getCurrentProject().findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_INFINITE);
        Assert.assertEquals((Object) 6, (Object) markers.length);

        runAction(new RemoveStaticCoverageMarkersAction(), AlgorithmScope.FILE_SCOPE);

        IMarker[] newMarkers = getCurrentProject().findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_INFINITE);

        Assert.assertEquals((Object) 2, (Object) newMarkers.length);
        Assert.assertEquals((Object) 2, (Object) allSuggestions.size());
    }
}
