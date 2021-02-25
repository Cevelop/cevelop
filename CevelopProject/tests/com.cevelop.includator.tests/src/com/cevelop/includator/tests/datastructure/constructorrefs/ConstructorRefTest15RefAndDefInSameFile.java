/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.constructorrefs;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.ConstructorDeclarationReference;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.cxxelement.SpecialMemberFunctionDeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ConstructorRefTest15RefAndDefInSameFile extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 11, (Object) references.size());

        SpecialMemberFunctionDeclarationReference xConstrRef = getConstructorReference(references.get(5));
        Assert.assertTrue(xConstrRef.isOnlyReferenceName());
        Assert.assertTrue(xConstrRef.isOnlyDeclarationName());
        Assert.assertTrue(xConstrRef.isDefinitionName());

        ConstructorDeclarationReference yConstrRef = getConstructorReference(references.get(10));
        Assert.assertTrue(yConstrRef.isImplicit());
        Assert.assertTrue(yConstrRef.isOnlyReferenceName());
        assertDeclarationReference("Y", "A.cpp", 78, yConstrRef);
        assertNodeSignature("X", yConstrRef.getASTNode());// the helper name is the constructor definition of X
        assertDeclaration(getRequiredDependency(yConstrRef).getDeclaration(), "Y.h", "Y", 6);
    }

}
