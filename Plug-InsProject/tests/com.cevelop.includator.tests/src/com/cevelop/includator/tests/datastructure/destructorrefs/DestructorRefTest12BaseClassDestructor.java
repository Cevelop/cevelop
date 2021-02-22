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


public class DestructorRefTest12BaseClassDestructor extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 8, (Object) references.size());

        DestructorDeclarationReference aDestrRef = getDestructorReference(references.get(6));
        Assert.assertTrue(aDestrRef instanceof DestructorDeclarationReference);
        Assert.assertTrue(aDestrRef.isOnlyReferenceName());
        assertDeclarationReference("~A", "main.cpp", 21, aDestrRef);
        assertNodeSignature("B", aDestrRef.getASTNode());// the helper name is the destr definition of B
        assertDeclaration(getRequiredDependency(aDestrRef).getDeclaration(), "A.h", "~A", 20);
    }
}
