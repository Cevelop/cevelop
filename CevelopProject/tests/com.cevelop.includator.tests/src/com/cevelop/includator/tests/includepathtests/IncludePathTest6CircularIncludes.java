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


public class IncludePathTest6CircularIncludes extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> refs = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 7, (Object) refs.size());

        DeclarationReference ref = refs.get(4);
        Assert.assertEquals("D", ref.getASTNode().getRawSignature());

        List<FullIncludePath> includePaths = getRequiredDependency(ref).getIncludePaths();
        Assert.assertEquals((Object) 1, (Object) includePaths.size());
        assertIncludePath("D.h", includePaths.get(0));
    }
}
