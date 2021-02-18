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


public class ProjectCoverageTest27OverrideMethod4 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 9, (Object) suggestions.size());

        String notInUseText = "This declaration is not in use through the file A.cpp.";
        String inUseText = "This declaration is in use through the file A.cpp.";
        String implicitlyInUseText = "This declaration is implicitly in use through the file A.cpp.";
        int i = 0;
        assertSuggestion(suggestions.get(i++), "Base.h", implicitlyInUseText, 20, 10); // class Base
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 23, 75); // int main() {
        assertSuggestion(suggestions.get(i++), "Child.h", inUseText, 19, 19); // void fooChild() {
        assertSuggestion(suggestions.get(i++), "Child.h", inUseText, 40, 11); // class Child
        assertSuggestion(suggestions.get(i++), "Child.h", inUseText, 78, 34); // virtual void foo() { fooChild(); }
        assertSuggestion(suggestions.get(i++), "SubChild.h", inUseText, 20, 14); // class SubChild
        assertSuggestion(suggestions.get(i++), "SubChild.h", inUseText, 62, 23); // virtual void foo() = 0;
        assertSuggestion(suggestions.get(i++), "Base.h", notInUseText, 0, 18); // void fooBase() {
        assertSuggestion(suggestions.get(i++), "Base.h", notInUseText, 43, 33); // virtual void foo() { fooBase(); }
    }
}
