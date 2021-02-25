/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.directincludedeclarationstests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.directlyincludereferenceddeclaration.DirectlyIncludeReferencedDeclarationsAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DirectIncludeDecl9RetainSystemInclude extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("fakeStdLib");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new DirectlyIncludeReferencedDeclarationsAlgorithm());

        Assert.assertEquals("Too few declaration references, check whether all references could be resolved in test file.", 10,
                getActiveFileDeclarationReferences().size());
        Assert.assertEquals((Object) 0, (Object) suggestions.size());
    }
}
