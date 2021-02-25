/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.organizeincludes;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class OrganizeIncludes21ReferencedProjectDoubleQuoteInclude extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("externalFrameworkTest");
        super.initAdditionalIncludes();
    }

    @Override
    protected void initReferencedProjects() throws Exception {
        stageReferencedProjectForBothProjects("theLib", "OrganizeIncludes21ReferencedProjectDoubleQuoteInclude_p2.rts");
        super.initReferencedProjects();
    }

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new OrganizeIncludesAlgorithm());

        Assert.assertEquals((Object) 2, (Object) suggestions.size());
        assertSuggestion(suggestions.get(0), "main.cpp", "Missing '#include \"lib.h\"'.", 0, 0);
        assertSuggestion(suggestions.get(1), "main.cpp", "Missing '#include <vector>'.", 0, 0);
    }
}
