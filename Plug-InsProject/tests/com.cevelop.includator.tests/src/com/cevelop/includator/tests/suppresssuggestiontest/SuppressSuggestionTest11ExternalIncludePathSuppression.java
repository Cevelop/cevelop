/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.suppresssuggestiontest;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;


public class SuppressSuggestionTest11ExternalIncludePathSuppression extends SuppressSuggestionTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("fakeStdLib");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        Suggestion.SOLUTION_OPERATION_OMIT_IN_FUTURE.executeOn(runAlgorithm(new OrganizeIncludesAlgorithm()));
        assertSuppressList("{main.cpp=[vector]}");
        Assert.assertEquals((Object) 0, (Object) runAlgorithm(new OrganizeIncludesAlgorithm()).size());
        assertStatus(
                "Prevented adding of suggestion 'Missing '#include <vector>'.' caused by entry in Suppress-Suggestion list (see project properties).");
    }
}
