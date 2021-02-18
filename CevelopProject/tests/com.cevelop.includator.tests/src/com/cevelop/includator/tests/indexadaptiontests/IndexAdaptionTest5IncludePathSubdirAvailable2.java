/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.indexadaptiontests;

import java.util.List;

import org.eclipse.core.runtime.MultiStatus;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class IndexAdaptionTest5IncludePathSubdirAvailable2 extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("IndexAdaptionTest4IncludePathSubdirAvailable");
        addIncludePathsSubDir("sub1");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        MultiStatus status = runAlgorithmsAsAction(new OrganizeIncludesAlgorithm(), AlgorithmScope.FILE_SCOPE);
        assertStatusOk(status);

        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();

        Assert.assertEquals((Object) 2, (Object) suggestions.size());
        assertSuggestion(suggestions.get(0), "A.cpp", "Missing '#include <QWER.h>'.", 0, 0);
        assertSuggestion(suggestions.get(1), "A.cpp", "Missing '#include <sub1/Sub.h>'.", 0, 0);
    }
}
