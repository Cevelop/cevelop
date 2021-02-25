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


public class ProjectCoverageTest20FieldConstrDestr4 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 12, (Object) suggestions.size());

        String notInUseText = "This declaration is not in use through the file A.cpp.";
        String inUseText = "This declaration is in use through the file A.cpp.";
        String implicitlyInUseText = "This declaration is implicitly in use through the file A.cpp.";
        int i = 0;
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 0, 7); // class Y
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 27, 5); // ~Y();
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 49, 11); // Y::~Y() { }
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 90, 5); // ~X();
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 98, 4); // Y y;
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 119, 11); // X::~X() { }
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 62, 7); // class X
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 132, 46); // int main() {
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 20, 4); // Y();
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 37, 10); // Y::Y() { }
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 83, 4); // X();
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 107, 10); // X::X() { }
    }
}
