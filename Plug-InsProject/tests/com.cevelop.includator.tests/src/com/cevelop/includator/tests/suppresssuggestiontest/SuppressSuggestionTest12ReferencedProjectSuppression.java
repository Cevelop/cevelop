/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.suppresssuggestiontest;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;


public class SuppressSuggestionTest12ReferencedProjectSuppression extends SuppressSuggestionTest {

    @Override
    protected void initReferencedProjects() throws Exception {
        stageReferencedProjectForBothProjects("otherProject", "SuppressSuggestionTest12ReferencedProjectSuppression_p2.rts");
        super.initReferencedProjects();
    }

    @Test
    public void runTest() throws Throwable {
        Suggestion.SOLUTION_OPERATION_OMIT_IN_FUTURE.executeOn(runAlgorithm(new OrganizeIncludesAlgorithm()));
        assertSuppressList("{main.cpp=[A.h]}");
        Assert.assertEquals((Object) 0, (Object) runAlgorithm(new OrganizeIncludesAlgorithm()).size());
        assertStatus(
                "Prevented adding of suggestion 'Missing '#include \"A.h\"'.' caused by entry in Suppress-Suggestion list (see project properties).");
    }
}
