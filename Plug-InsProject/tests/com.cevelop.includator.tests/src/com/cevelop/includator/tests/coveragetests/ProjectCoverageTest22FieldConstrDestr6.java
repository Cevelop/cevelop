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


public class ProjectCoverageTest22FieldConstrDestr6 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 24, (Object) suggestions.size());

        String notInUseText = "This declaration is not in use through the file A.cpp.";
        String inUseText = "This declaration is in use through the file A.cpp.";
        String implicitlyInUseText = "This declaration is implicitly in use through the file A.cpp.";
        int i = 0;

        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 0, 7); // class Z
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 21, 4); // Z();
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 38, 10); // Z::Z() { }
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 63, 8); // class Y1
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 84, 5); // Y1();
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 101, 4); // Z z;
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 110, 12); // Y1::Y1() { }
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 139, 8); // class Y2
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 160, 5); // Y2();
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 179, 12); // Y2::Y2() { }
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 229, 4); // X();
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 244, 6); // Y1 y1;
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 253, 6); // Y2 y2;
        assertSuggestion(suggestions.get(i++), "A.cpp", implicitlyInUseText, 264, 10); // X::X() { }
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 208, 7); // class X
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 289, 44); // int main() {
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 28, 5); // ~Z();
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 50, 11); // Z::~Z() { }
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 92, 6); // ~Y1();
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 124, 13); // Y1::~Y1() { }
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 168, 6); // ~Y2();
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 193, 13); // Y2::~Y2() { }
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 236, 5); // ~X();
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 276, 11); // X::~X() { }
    }
}
