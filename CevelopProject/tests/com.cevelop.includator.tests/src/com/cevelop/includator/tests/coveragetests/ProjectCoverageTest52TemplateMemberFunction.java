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


public class ProjectCoverageTest52TemplateMemberFunction extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new StaticCoverageProjectAnalysisAlgorithm());
        Assert.assertEquals((Object) 3, (Object) suggestions.size());

        String inUseText = "This declaration is in use through the file main.cpp.";
        int i = 0;
        assertSuggestion(suggestions.get(i++), "main.cpp", inUseText, 21, 7); // class V
        assertSuggestion(suggestions.get(i++), "main.cpp", inUseText, 64, 22); // void foo(Iter i0){}
        assertSuggestion(suggestions.get(i++), "main.cpp", inUseText, 91, 36); // int main() {
    }
}
