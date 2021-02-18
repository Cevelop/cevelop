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


public class ProjectCoverageTest51WithIntMainVoid extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());

        Assert.assertEquals((Object) 8, (Object) suggestions.size());
        String inUseText = "This declaration is in use through the file A.cpp.";
        String notInUseText = "This declaration is not in use through the file A.cpp.";

        int i = 0;
        assertSuggestion(suggestions.get(i++), "A.cpp", inUseText, 39, 45); // int main() {
        assertSuggestion(suggestions.get(i++), "Used.cpp", inUseText, 17, 25); // void X::foo() {
        assertSuggestion(suggestions.get(i++), "Used.h", inUseText, 0, 7); // class X
        assertSuggestion(suggestions.get(i++), "Used.h", inUseText, 21, 11); // void foo();
        assertSuggestion(suggestions.get(i++), "X2.h", inUseText, 0, 8); // class X2
        assertSuggestion(suggestions.get(i++), "Other.h", notInUseText, 0, 11); // class Other
        assertSuggestion(suggestions.get(i++), "Unused.h", notInUseText, 0, 12); // class Unused
        assertSuggestion(suggestions.get(i++), "otherSource.cpp", notInUseText, 20, 39); // void someOtherFunction() {
    }
}
