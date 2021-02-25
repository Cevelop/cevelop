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


public class ProjectCoverageTest32RecursionTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 7, (Object) suggestions.size());

        String inUseText = "This declaration is in use through the file main.cpp.";
        String implicitlyInUseText = "This declaration is implicitly in use through the file main.cpp.";
        int i = 0;
        assertSuggestion(suggestions.get(i++), "X.cpp", implicitlyInUseText, 16, 24); // X::X() {
        assertSuggestion(suggestions.get(i++), "X.cpp", implicitlyInUseText, 42, 11); // X::~X() {
        assertSuggestion(suggestions.get(i++), "X.h", implicitlyInUseText, 19, 4); // X();
        assertSuggestion(suggestions.get(i++), "X.h", implicitlyInUseText, 25, 13); // virtual ~X();
        assertSuggestion(suggestions.get(i++), "X.h", implicitlyInUseText, 40, 5); // X *x;
        assertSuggestion(suggestions.get(i++), "X.h", inUseText, 0, 7); // class X
        assertSuggestion(suggestions.get(i++), "main.cpp", inUseText, 16, 31); // int main() {
    }
}
