/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.multipledefinition;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class MultipleDefinitionTest6Function extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 2, (Object) references.size());
        DeclarationReference classXRef = references.get(1);
        Assert.assertTrue(classXRef.isFunctionReference());
        assertDeclRefDependencyTargetFile("a.h", classXRef.getRequiredDependencies());
        assertStatus("Found multiple definitions of foo in main.cpp[2:3,2:6]. Prefering foo in a.h[1:6,1:9]. Candidates are:", "a.h[1:6,1:9]",
                "b.h[1:6,1:9]");
    }
}
