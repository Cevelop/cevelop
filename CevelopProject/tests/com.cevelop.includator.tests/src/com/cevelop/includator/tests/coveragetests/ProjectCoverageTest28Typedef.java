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


public class ProjectCoverageTest28Typedef extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());
        Assert.assertEquals((Object) 11, (Object) suggestions.size());

        String notInUseText = "This declaration is not in use through the file main.cpp.";
        String inUseText = "This declaration is in use through the file main.cpp.";
        String implicitlyInUseText = "This declaration is implicitly in use through the file main.cpp.";
        int i = 0;
        assertSuggestion(suggestions.get(i++), "X2.cpp", implicitlyInUseText, 17, 12); // X2::X2() { }
        assertSuggestion(suggestions.get(i++), "X2.cpp", implicitlyInUseText, 31, 13); // X2::~X2() { }
        assertSuggestion(suggestions.get(i++), "X2.h", implicitlyInUseText, 20, 5); // X2();
        assertSuggestion(suggestions.get(i++), "X2.h", implicitlyInUseText, 27, 14); // virtual ~X2();
        assertSuggestion(suggestions.get(i++), "X1.h", inUseText, 17, 14); // typedef X2 X1;
        assertSuggestion(suggestions.get(i++), "X2.cpp", inUseText, 66, 18); // void X2::foo() { }
        assertSuggestion(suggestions.get(i++), "X2.h", inUseText, 0, 8); // class X2
        assertSuggestion(suggestions.get(i++), "X2.h", inUseText, 43, 11); // void foo();
        assertSuggestion(suggestions.get(i++), "main.cpp", inUseText, 17, 33); // int main() {
        assertSuggestion(suggestions.get(i++), "X2.cpp", notInUseText, 46, 18); // void X2::bar() { }
        assertSuggestion(suggestions.get(i++), "X2.h", notInUseText, 56, 11); // void bar();
    }
}
