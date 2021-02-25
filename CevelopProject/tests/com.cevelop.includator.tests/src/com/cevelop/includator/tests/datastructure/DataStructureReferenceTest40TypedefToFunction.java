/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest40TypedefToFunction extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 5, (Object) references.size());

        DeclarationReference functionTypedef = references.get(0);
        assertDeclaration(getRequiredDependency(functionTypedef).getDeclaration(), "src/main.cpp", "IntProducer", 35);
        Assert.assertTrue(!functionTypedef.isForwardDeclarationEnough());

        DeclarationReference typedefRef = references.get(2);
        assertDeclaration(getRequiredDependency(typedefRef).getDeclaration(), "src/main.cpp", "intMine", 78);
        Assert.assertTrue(!typedefRef.isForwardDeclarationEnough());

        DeclarationReference randomRef = references.get(3);
        assertDeclaration(getRequiredDependency(randomRef).getDeclaration(), "src/random.h", "random", 4);
        Assert.assertTrue(randomRef.isForwardDeclarationEnough());

    }
}
