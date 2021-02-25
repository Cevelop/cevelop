/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.coveragetests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.staticcoverage.StaticCoverageProjectAnalysisAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ProjectCoverageTest3NameSpaces extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 3, (Object) suggestions.size());
        int i = 0;
        assertSuggestion(suggestions.get(i++), "A.cpp", "This declaration is in use.", 39, 45);
        assertSuggestion(suggestions.get(i++), "Used.h", "This declaration is in use.", 14, 7);
        assertSuggestion(suggestions.get(i++), "Unused.h", "This declaration is not in use.", 14, 12);
    }
}
