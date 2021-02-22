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


public class ProjectCoverageTest6PointerToMember extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());
        printSuggestionsToJavaCode(suggestions);
        Assert.assertEquals((Object) 4, (Object) suggestions.size());
        String inUseText = "This declaration is in use through the file A.cpp.";
        int i = 0;
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 1, 7); // class X
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 21, 9); // void f();
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 35, 14); // void X::f(){
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 52, 96); // int main() {
    }
}
