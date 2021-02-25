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


public class ProjectCoverageTest48TemplPartialSpecialization extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());
        // printSuggestionsToJavaCode(suggestions);

        Assert.assertEquals((Object) 24, (Object) suggestions.size());

        String notInUseText = "This declaration is not in use through the file main.cpp.";
        String inUseText = "This declaration is in use through the file main.cpp.";
        String implicitlyInUseText = "This declaration is implicitly in use through the file main.cpp.";
        int i = 0;
        assertSuggestion(suggestions.get(i++), "main.cpp", implicitlyInUseText, 38, 16); // class Collection
        assertSuggestion(suggestions.get(i++), "main.cpp", implicitlyInUseText, 66, 14); // Collection(){}
        assertSuggestion(suggestions.get(i++), "main.cpp", implicitlyInUseText, 82, 15); // ~Collection(){}
        assertSuggestion(suggestions.get(i++), "main.cpp", implicitlyInUseText, 285, 14); // Collection(){}
        assertSuggestion(suggestions.get(i++), "main.cpp", implicitlyInUseText, 301, 15); // ~Collection(){}
        assertSuggestion(suggestions.get(i++), "main.cpp", implicitlyInUseText, 374, 31); // class Collection<float, length>
        assertSuggestion(suggestions.get(i++), "main.cpp", implicitlyInUseText, 417, 14); // Collection(){}
        assertSuggestion(suggestions.get(i++), "main.cpp", implicitlyInUseText, 433, 15); // ~Collection(){}
        assertSuggestion(suggestions.get(i++), "main.cpp", inUseText, 249, 24); // class Collection<int, 5>
        assertSuggestion(suggestions.get(i++), "main.cpp", inUseText, 318, 25); // int get(){
        assertSuggestion(suggestions.get(i++), "main.cpp", inUseText, 728, 169); // int main(){
        assertSuggestion(suggestions.get(i++), "main.cpp", notInUseText, 129, 29); // class Collection<int, length>
        assertSuggestion(suggestions.get(i++), "main.cpp", notInUseText, 170, 14); // Collection(){}
        assertSuggestion(suggestions.get(i++), "main.cpp", notInUseText, 186, 15); // ~Collection(){}
        assertSuggestion(suggestions.get(i++), "main.cpp", notInUseText, 203, 30); // int get(){
        assertSuggestion(suggestions.get(i++), "main.cpp", notInUseText, 464, 26); // class Collection<float, 5>
        assertSuggestion(suggestions.get(i++), "main.cpp", notInUseText, 502, 14); // Collection(){}
        assertSuggestion(suggestions.get(i++), "main.cpp", notInUseText, 518, 15); // ~Collection(){}
        assertSuggestion(suggestions.get(i++), "main.cpp", notInUseText, 565, 30); // class Collection<bool, length>
        assertSuggestion(suggestions.get(i++), "main.cpp", notInUseText, 607, 14); // Collection(){}
        assertSuggestion(suggestions.get(i++), "main.cpp", notInUseText, 623, 15); // ~Collection(){}
        assertSuggestion(suggestions.get(i++), "main.cpp", notInUseText, 655, 25); // class Collection<bool, 9>
        assertSuggestion(suggestions.get(i++), "main.cpp", notInUseText, 692, 14); // Collection(){}
        assertSuggestion(suggestions.get(i++), "main.cpp", notInUseText, 708, 15); // ~Collection(){}
    }
}
