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


public class ProjectCoverageTest30ImplicitlyUsed2 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 19, (Object) suggestions.size());

        String inUseText = "This declaration is in use through the file main.cpp.";
        String implicitlyInUseText = "This declaration is implicitly in use through the file main.cpp.";
        int i = 0;

        assertSuggestion(suggestions.get(i++), "X.h", implicitlyInUseText, 61, 13); // X() : Y() { }
        assertSuggestion(suggestions.get(i++), "X.h", implicitlyInUseText, 77, 8); // ~X() { }
        assertSuggestion(suggestions.get(i++), "X.h", implicitlyInUseText, 88, 4); // Z z;
        assertSuggestion(suggestions.get(i++), "Y.cpp", implicitlyInUseText, 16, 14); // void foo() { }
        assertSuggestion(suggestions.get(i++), "Y.cpp", implicitlyInUseText, 32, 17); // Y::Y() { foo(); }
        assertSuggestion(suggestions.get(i++), "Y.cpp", implicitlyInUseText, 50, 11); // Y::~Y() { }
        assertSuggestion(suggestions.get(i++), "Y.h", implicitlyInUseText, 0, 11); // void foo();
        assertSuggestion(suggestions.get(i++), "Y.h", implicitlyInUseText, 13, 7); // class Y
        assertSuggestion(suggestions.get(i++), "Y.h", implicitlyInUseText, 33, 4); // Y();
        assertSuggestion(suggestions.get(i++), "Y.h", implicitlyInUseText, 40, 5); // ~Y();
        assertSuggestion(suggestions.get(i++), "Z.cpp", implicitlyInUseText, 16, 14); // void bar() { }
        assertSuggestion(suggestions.get(i++), "Z.cpp", implicitlyInUseText, 32, 17); // Z::Z() { bar(); }
        assertSuggestion(suggestions.get(i++), "Z.cpp", implicitlyInUseText, 50, 11); // Z::~Z() { }
        assertSuggestion(suggestions.get(i++), "Z.h", implicitlyInUseText, 0, 11); // void bar();
        assertSuggestion(suggestions.get(i++), "Z.h", implicitlyInUseText, 13, 7); // class Z
        assertSuggestion(suggestions.get(i++), "Z.h", implicitlyInUseText, 33, 4); // Z();
        assertSuggestion(suggestions.get(i++), "Z.h", implicitlyInUseText, 40, 5); // ~Z();
        assertSuggestion(suggestions.get(i++), "X.h", inUseText, 30, 7); // class X
        assertSuggestion(suggestions.get(i++), "main.cpp", inUseText, 16, 42); // int main() {
    }
}
