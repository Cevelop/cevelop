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


public class ProjectCoverageTest33PointerField extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 8, (Object) suggestions.size());

        String notInUseText = "This declaration is not in use through the file A.cpp.";
        String inUseText = "This declaration is in use through the file A.cpp.";
        String implicitlyInUseText = "This declaration is implicitly in use through the file A.cpp.";
        int i = 0;
        assertSuggestion(suggestions.get(i++), "X.cpp", implicitlyInUseText, 16, 10); // X::X() { }
        assertSuggestion(suggestions.get(i++), "X.cpp", implicitlyInUseText, 27, 11); // X::~X() { }
        assertSuggestion(suggestions.get(i++), "X.h", implicitlyInUseText, 34, 4); // X();
        assertSuggestion(suggestions.get(i++), "X.h", implicitlyInUseText, 40, 13); // virtual ~X();
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 16, 31); // int main() {
        assertSuggestion(suggestions.get(i++), "X.h", inUseText, 15, 7); // class X
        assertSuggestion(suggestions.get(i++), "X.h", notInUseText, 55, 10); // Y * field;
        assertSuggestion(suggestions.get(i++), "Y.h", notInUseText, 0, 7); // class Y
    }
}
