/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.referencedProjectsTest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ReferencedProject1TwoProjectsSuggestionTest extends IncludatorTest {

    @Override
    protected void initReferencedProjects() throws Exception {
        stageReferencedProjectForBothProjects("otherProject", "ReferencedProject1TwoProjectsSuggestionTest_p2.rts");
        super.initReferencedProjects();
    }

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new OrganizeIncludesAlgorithm());
        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        assertSuggestion(suggestions.get(0), "main.cpp", "Missing '#include \"A.h\"'.", 0, 0);
    }
}
