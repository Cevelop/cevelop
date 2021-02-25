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


public class ProjectCoverageTest11DirectlyImplClass extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 11, (Object) suggestions.size());

        String notInUseText = "This declaration is not in use through the file A.cpp.";
        String inUseText = "This declaration is in use through the file A.cpp.";
        String implicitlyInUseText = "This declaration is implicitly in use through the file A.cpp.";
        int i = 0;
        assertSuggestion(suggestions.get(i++), "X.cpp", implicitlyInUseText, 16, 10); // X::X() { }
        assertSuggestion(suggestions.get(i++), "X.cpp", implicitlyInUseText, 28, 11); // X::~X() { }
        assertSuggestion(suggestions.get(i++), "X.h", implicitlyInUseText, 19, 4); // X();
        assertSuggestion(suggestions.get(i++), "X.h", implicitlyInUseText, 25, 13); // virtual ~X();
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 16, 42); // int main() {
        assertSuggestion(suggestions.get(i++), "X.h", inUseText, 0, 7); // class X
        assertSuggestion(suggestions.get(i++), "X.h", inUseText, 53, 15); // void foo2() { }
        assertSuggestion(suggestions.get(i++), "X.cpp", notInUseText, 41, 17); // void X::foo() { }
        assertSuggestion(suggestions.get(i++), "X.h", notInUseText, 40, 11); // void foo();
        assertSuggestion(suggestions.get(i++), "X.h", notInUseText, 70, 15); // void foo3() { }
        assertSuggestion(suggestions.get(i++), "X.h", notInUseText, 87, 10); // int field;
    }
}
