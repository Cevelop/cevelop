/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.includepathtests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.FullIncludePath;
import com.cevelop.includator.tests.base.IncludatorTest;


public class IncludePathTest5ManyPaths extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("externalFrameworkTest");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> refs = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 11, (Object) refs.size());

        DeclarationReference ref = refs.get(4);
        Assert.assertEquals("E", ref.getASTNode().getRawSignature());

        List<FullIncludePath> includePaths = getRequiredDependency(ref).getIncludePaths();
        Assert.assertEquals((Object) 5, (Object) includePaths.size());
        assertIncludePath("E.h", includePaths.get(0));
        assertIncludePath("F.h -> E.h", includePaths.get(1));
        assertIncludePath("G.h -> F.h -> E.h", includePaths.get(2));
        assertIncludePath("G.h -> I.h -> K.h -> E.h", includePaths.get(3));
        assertIncludePath("H.h -> I.h -> K.h -> E.h", includePaths.get(4));
    }
}
