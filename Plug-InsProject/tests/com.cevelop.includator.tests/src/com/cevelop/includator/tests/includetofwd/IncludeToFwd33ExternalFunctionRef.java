/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.includetofwd;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.includestofwd.ReplaceIncludesWithFwdAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class IncludeToFwd33ExternalFunctionRef extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("fakeStdLib");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> refs = getActiveFileDeclarationReferences();
        Assert.assertEquals((Object) 2, (Object) refs.size());
        DeclarationReference printfRef = refs.get(1);
        assertDeclRefDependencyExternalTargetFile("fakeStdLib/stdio.h", printfRef.getRequiredDependencies());
        Assert.assertTrue(printfRef.isForwardDeclarationEnough());
        List<Suggestion<?>> suggestions = runAlgorithm(new ReplaceIncludesWithFwdAlgorithm());
        assertStatusOk();
        Assert.assertEquals((Object) 0, (Object) suggestions.size()); // don't propose to forward declare sdk functions.
    }
}
