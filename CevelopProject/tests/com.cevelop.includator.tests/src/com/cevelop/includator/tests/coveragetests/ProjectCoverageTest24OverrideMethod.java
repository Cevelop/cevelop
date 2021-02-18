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


public class ProjectCoverageTest24OverrideMethod extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 5, (Object) suggestions.size());

        String notInUseText = "This declaration is not in use through the file A.cpp.";
        String inUseText = "This declaration is in use through the file A.cpp.";
        String implicitlyInUseText = "This declaration is implicitly in use through the file A.cpp.";
        int i = 0;
        assertSuggestion(suggestions.get(i++), "A.h", implicitlyInUseText, 0, 7); // class A
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 16, 44); // int main() {
        assertSuggestion(suggestions.get(i++), "B.h", inUseText, 15, 7); // class B
        assertSuggestion(suggestions.get(i++), "B.h", inUseText, 46, 22); // virtual void foo() { }
        assertSuggestion(suggestions.get(i++), "A.h", notInUseText, 20, 22); // virtual void foo() { }
    }
}
