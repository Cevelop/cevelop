/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.declreftests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DeclRefTest14CLinkageDesignatedInitializerTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 3, (Object) references.size());
        assertDeclarationReference("B", "a.c", references.get(0));
        assertDeclarationReference("b", "a.c", references.get(1));
        DeclarationReference fRef = references.get(2);
        assertDeclarationReference("f", "a.c", 49, fRef);
        assertDeclRefDependencyTargetFile("b.h", fRef.getRequiredDependencies());
        assertStatusOk();
    }
}
