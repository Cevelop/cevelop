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


public class ProjectCoverageTest13GlobalVar3 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 6, (Object) suggestions.size());
        int i = 0;
        String notInUseText = "This declaration is not in use through the file A.cpp.";
        String inUseText = "This declaration is in use through the file A.cpp.";

        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 0, 24); // int main() { return 0; }
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 26, 7); // class Y
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 59, 11); // static Y y;
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 40, 7); // class X
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 72, 23); // static int fooStatic();
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 100, 33); // int X::fooStatic() {
    }
}
