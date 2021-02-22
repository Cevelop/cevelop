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


public class ProjectCoverageTest23ParentClassMethod extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 4, (Object) suggestions.size());

        String inUseText = "This declaration is in use through the file A.cpp.";
        int i = 0;
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 16, 44); // int main() {
        assertSuggestion(suggestions.get(i++), "A.h", inUseText, 0, 7); // class A
        assertSuggestion(suggestions.get(i++), "A.h", inUseText, 20, 14); // void foo() { }
        assertSuggestion(suggestions.get(i++), "B.h", inUseText, 15, 7); // class B
    }
}
