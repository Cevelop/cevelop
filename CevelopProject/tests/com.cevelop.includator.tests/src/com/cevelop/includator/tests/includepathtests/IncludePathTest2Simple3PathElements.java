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


public class IncludePathTest2Simple3PathElements extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> refs = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 7, (Object) refs.size());

        DeclarationReference ref = refs.get(4);
        assertDeclRefName("D", ref);

        FullIncludePath includePath = getIncludePath(getRequiredDependency(ref));
        assertIncludePath("B.h -> C.h -> D.h", includePath);
    }
}
