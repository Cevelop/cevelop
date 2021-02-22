/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.destructorrefs;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.cxxelement.DestructorDeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DestructorRefTest14BaseClassDestructor3 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 12, (Object) references.size());

        DestructorDeclarationReference a1DestrRef = getDestructorReference(references.get(10));
        Assert.assertTrue(a1DestrRef.isOnlyReferenceName());
        assertDeclarationReference("~A1", "B.cpp", 51, a1DestrRef);
        assertNodeSignature("~B", a1DestrRef.getASTNode());// the helper name is the destr definition of B
        assertDeclaration(getRequiredDependency(a1DestrRef).getDeclaration(), "A.h", "~A1", 21);

        DestructorDeclarationReference a2DestrRef = getDestructorReference(references.get(11));
        Assert.assertTrue(a2DestrRef.isOnlyReferenceName());
        assertDeclarationReference("~A2", "B.cpp", 51, a2DestrRef);
        assertNodeSignature("~B", a2DestrRef.getASTNode());// the helper name is the destr definition of B
        assertDeclaration(getRequiredDependency(a2DestrRef).getDeclaration(), "A.h", "~A2", 55);
    }
}
