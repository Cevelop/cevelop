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


public class ProjectCoverageTest5SkipImplWhenFwdEnough extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 10, (Object) suggestions.size());
        int i = 0;
        String inUseText = "This declaration is in use through the file A.cpp.";
        String notInUseText = "This declaration is not in use through the file A.cpp.";
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 16, 8); // class X;
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 26, 32); // int main() {
        assertSuggestion(suggestions.get(i++), "X.cpp", notInUseText, 16, 10); // X::X() { }
        assertSuggestion(suggestions.get(i++), "X.cpp", notInUseText, 28, 11); // X::~X() { }
        assertSuggestion(suggestions.get(i++), "X.cpp", notInUseText, 41, 17); // void X::foo() { }
        assertSuggestion(suggestions.get(i++), "X.h", notInUseText, 0, 7); // class X
        assertSuggestion(suggestions.get(i++), "X.h", notInUseText, 19, 4); // X();
        assertSuggestion(suggestions.get(i++), "X.h", notInUseText, 25, 13); // virtual ~X();
        assertSuggestion(suggestions.get(i++), "X.h", notInUseText, 40, 11); // void foo();
        assertSuggestion(suggestions.get(i++), "X.h", notInUseText, 53, 10); // int field;
    }
}
