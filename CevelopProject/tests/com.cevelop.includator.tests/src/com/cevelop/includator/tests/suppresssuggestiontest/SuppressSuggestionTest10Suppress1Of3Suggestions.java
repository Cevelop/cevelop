/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.suppresssuggestiontest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.tests.mocks.CustomSuggestionSelectedAction;


public class SuppressSuggestionTest10Suppress1Of3Suggestions extends SuppressSuggestionTest {

    @Test
    public void runTest() throws Throwable {
        CustomSuggestionSelectedAction action = new CustomSuggestionSelectedAction(new OrganizeIncludesAlgorithm());
        action.addExpectedSelection(Suggestion.SOLUTION_OPERATION_DO_NOTHING, "Missing '#include \"A.h\"'.", "Missing '#include \"C.h\"'.");
        action.addExpectedSelection(Suggestion.SOLUTION_OPERATION_OMIT_IN_FUTURE, "Missing '#include \"B.h\"'.");
        runAction(action, AlgorithmScope.FILE_SCOPE);
        assertSuppressList("{main.cpp=[B.h]}");
        List<Suggestion<?>> suggestion = runAlgorithm(new OrganizeIncludesAlgorithm());
        Assert.assertEquals((Object) 2, (Object) suggestion.size());
        assertSuggestion(suggestion.get(0), "main.cpp", "Missing '#include \"A.h\"'.", 0, 0);
        assertSuggestion(suggestion.get(1), "main.cpp", "Missing '#include \"C.h\"'.", 0, 0);
        assertAllSourceFilesEqual(COMPARE_AST_AND_COMMENTS_AND_INCLUDES);
        assertStatus(
                "Prevented adding of suggestion 'Missing '#include \"B.h\"'.' caused by entry in Suppress-Suggestion list (see project properties).");
    }
}
