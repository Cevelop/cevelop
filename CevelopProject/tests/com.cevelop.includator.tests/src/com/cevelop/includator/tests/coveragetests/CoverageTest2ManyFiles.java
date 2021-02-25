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


public class CoverageTest2ManyFiles extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 3, (Object) suggestions.size());
        String expectedTextInUse = "This declaration is in use through the file A.cpp.";
        String expectedTextNotInUse = "This declaration is not in use through the file A.cpp.";
        assertSuggestion(suggestions.get(0), "A.cpp", expectedTextInUse, 39, 31);
        assertSuggestion(suggestions.get(1), "Used.h", expectedTextInUse, 0, 7);
        assertSuggestion(suggestions.get(2), "Unused.h", expectedTextNotInUse, 0, 12);
    }
}
