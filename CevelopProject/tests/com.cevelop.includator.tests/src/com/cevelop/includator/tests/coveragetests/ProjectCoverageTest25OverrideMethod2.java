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


public class ProjectCoverageTest25OverrideMethod2 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 10, (Object) suggestions.size());

        String notInUseText = "This declaration is not in use through the file main.cpp.";
        String inUseText = "This declaration is in use through the file main.cpp.";
        String implicitlyInUseText = "This declaration is implicitly in use through the file main.cpp.";
        int i = 0;
        assertSuggestion(suggestions.get(i++), "Base.h", implicitlyInUseText, 20, 10); // class Base
        assertSuggestion(suggestions.get(i++), "Child.h", inUseText, 19, 19); // void fooChild() {
        assertSuggestion(suggestions.get(i++), "Child.h", inUseText, 40, 11); // class Child
        assertSuggestion(suggestions.get(i++), "Child.h", inUseText, 78, 34); // virtual void foo() { fooChild(); }
        assertSuggestion(suggestions.get(i++), "SubChild.h", inUseText, 20, 22); // void fooSubChild() {
        assertSuggestion(suggestions.get(i++), "SubChild.h", inUseText, 44, 14); // class SubChild
        assertSuggestion(suggestions.get(i++), "SubChild.h", inUseText, 86, 38); // virtual void foo() { fooSubChild();  }
        assertSuggestion(suggestions.get(i++), "main.cpp", inUseText, 23, 75); // int main() {
        assertSuggestion(suggestions.get(i++), "Base.h", notInUseText, 0, 18); // void fooBase() {
        assertSuggestion(suggestions.get(i++), "Base.h", notInUseText, 43, 33); // virtual void foo() { fooBase(); }
    }
}
