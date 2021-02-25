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
import com.cevelop.includator.tests.base.IncludatorTest;


public class ConstructorRefTest14ImplConstrImplicitSubConstr extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 9, (Object) references.size());

        ConstructorDeclarationReference yImpliciteDefaultConstrDefinitionRef = getConstructorReference(references.get(1));
        Assert.assertTrue(yImpliciteDefaultConstrDefinitionRef.isImplicit());
        Assert.assertTrue(yImpliciteDefaultConstrDefinitionRef.isDefinitionName());
        Assert.assertTrue(yImpliciteDefaultConstrDefinitionRef.isDefaultConstructor());
        assertDeclRefName("Y", yImpliciteDefaultConstrDefinitionRef);
        assertNodeSignature("Y", yImpliciteDefaultConstrDefinitionRef.getASTNode());
        assertDeclaration(getRequiredDependency(yImpliciteDefaultConstrDefinitionRef).getDeclaration(), "A.cpp", "Y", 22);

        ConstructorDeclarationReference yImpliciteCopyConstrDefinitionRef = getConstructorReference(references.get(3));
        Assert.assertTrue(yImpliciteCopyConstrDefinitionRef.isImplicit());
        Assert.assertTrue(yImpliciteCopyConstrDefinitionRef.isDefinitionName());
        Assert.assertFalse(yImpliciteCopyConstrDefinitionRef.isDefaultConstructor());
        assertDeclRefName("Y", yImpliciteCopyConstrDefinitionRef);
        assertNodeSignature("Y", yImpliciteCopyConstrDefinitionRef.getASTNode());
        assertDeclaration(getRequiredDependency(yImpliciteCopyConstrDefinitionRef).getDeclaration(), "A.cpp", "Y", 22);

        ConstructorDeclarationReference zImpliciteDefaultConstrDefinitionRef = getConstructorReference(references.get(2));
        Assert.assertTrue(zImpliciteDefaultConstrDefinitionRef.isImplicit());
        Assert.assertTrue(zImpliciteDefaultConstrDefinitionRef.isOnlyReferenceName());
        Assert.assertTrue(zImpliciteDefaultConstrDefinitionRef.isDefaultConstructor());
        assertDeclRefName("Z", zImpliciteDefaultConstrDefinitionRef);
        assertNodeSignature("Y", zImpliciteDefaultConstrDefinitionRef.getASTNode());
        assertDeclaration(getRequiredDependency(zImpliciteDefaultConstrDefinitionRef).getDeclaration(), "Z.h", "Z", 20);

        ConstructorDeclarationReference zImpliciteCopyConstrDefinitionRef = getConstructorReference(references.get(4));
        Assert.assertTrue(zImpliciteCopyConstrDefinitionRef.isImplicit());
        Assert.assertTrue(zImpliciteCopyConstrDefinitionRef.isOnlyReferenceName());
        Assert.assertFalse(zImpliciteCopyConstrDefinitionRef.isDefaultConstructor());
        assertDeclRefName("Z", zImpliciteCopyConstrDefinitionRef);
        assertNodeSignature("Y", zImpliciteCopyConstrDefinitionRef.getASTNode());
        assertDeclaration(getRequiredDependency(zImpliciteCopyConstrDefinitionRef).getDeclaration(), "Z.h", "Z", 27);
    }
}
