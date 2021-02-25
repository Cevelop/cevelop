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


public class ProjectCoverageTest14GlobalVar4 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 9, (Object) suggestions.size());

        String notInUseText = "This declaration is not in use through the file A.cpp.";
        String inUseText = "This declaration is in use through the file A.cpp.";
        int i = 0;
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 0, 24); // int main() { return 0; }
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 26, 7); // class Y
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 40, 7); // class X
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 65, 11); // static Y y;
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 78, 23); // static int fooStatic();
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 106, 33); // int X::fooStatic() {
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 154, 28); // int global = X::fooStatic();
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 59, 4); // X();
        assertSuggestion(suggestions.get(i++), "A.cpp", notInUseText, 141, 11); // X::X() {
    }
}
