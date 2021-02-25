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


public class ProjectCoverageTest16DestructorTest2 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 6, (Object) suggestions.size());

        String notInUseText = "This declaration is not in use through the file A.cpp.";
        String inUseText = "This declaration is in use through the file A.cpp.";
        String implicitlyInUseText = "This declaration is implicitly in use through the file A.cpp.";
        int i = 0;

        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 21, 4); // X();
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 38, 10); // X::X() { }
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 0, 7); // class X
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 63, 44); // int main() {
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 28, 5); // ~X();
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 50, 11); // X::~X() { }
    }
}
