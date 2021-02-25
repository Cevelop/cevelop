/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
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


public class ProjectCoverageTest50OverloadedOperator extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 4, (Object) suggestions.size());

        String inUseText = "This declaration is in use through the file " + makeOSPath("src/main.cpp.");
        String implicitlyInUseText = "This declaration is implicitly in use through the file " + makeOSPath("src/main.cpp.");
        int i = 0;
        assertSuggestion(suggestions.get(i++), makeOSPath("src/rational.h"), implicitlyInUseText, 26, 20); // Rational(int, int){}
        assertSuggestion(suggestions.get(i++), makeOSPath("src/rational.h"), implicitlyInUseText, 48, 75); // Rational
        // operator+(Rational
        // const & rhs){
        assertSuggestion(suggestions.get(i++), makeOSPath("src/main.cpp"), inUseText, 22, 90); // int main (){
        assertSuggestion(suggestions.get(i++), makeOSPath("src/rational.h"), inUseText, 0, 14); // class Rational
    }
}
