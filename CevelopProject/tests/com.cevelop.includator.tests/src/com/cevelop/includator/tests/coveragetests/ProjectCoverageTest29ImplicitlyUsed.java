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


public class ProjectCoverageTest29ImplicitlyUsed extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 15, (Object) suggestions.size());

        String inUseText = "This declaration is in use through the file main.cpp.";
        String implicitlyInUseText = "This declaration is implicitly in use through the file main.cpp.";
        int i = 0;

        assertSuggestion(suggestions.get(i++), "X.h", implicitlyInUseText, 61, 7); // X() { }
        assertSuggestion(suggestions.get(i++), "X.h", implicitlyInUseText, 71, 8); // ~X() { }
        assertSuggestion(suggestions.get(i++), "X.h", implicitlyInUseText, 82, 4); // Z z;
        assertSuggestion(suggestions.get(i++), "Y.cpp", implicitlyInUseText, 16, 10); // Y::Y() { }
        assertSuggestion(suggestions.get(i++), "Y.cpp", implicitlyInUseText, 27, 11); // Y::~Y() { }
        assertSuggestion(suggestions.get(i++), "Y.h", implicitlyInUseText, 0, 7); // class Y
        assertSuggestion(suggestions.get(i++), "Y.h", implicitlyInUseText, 20, 4); // Y();
        assertSuggestion(suggestions.get(i++), "Y.h", implicitlyInUseText, 27, 5); // ~Y();
        assertSuggestion(suggestions.get(i++), "Z.cpp", implicitlyInUseText, 16, 10); // Z::Z() { }
        assertSuggestion(suggestions.get(i++), "Z.cpp", implicitlyInUseText, 27, 11); // Z::~Z() { }
        assertSuggestion(suggestions.get(i++), "Z.h", implicitlyInUseText, 0, 7); // class Z
        assertSuggestion(suggestions.get(i++), "Z.h", implicitlyInUseText, 20, 4); // Z();
        assertSuggestion(suggestions.get(i++), "Z.h", implicitlyInUseText, 27, 5); // ~Z();
        assertSuggestion(suggestions.get(i++), "X.h", inUseText, 30, 7); // class X
        assertSuggestion(suggestions.get(i++), "main.cpp", inUseText, 16, 42); // int main() {
    }
}
