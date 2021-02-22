/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
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

import com.cevelop.includator.cxxelement.ConstructorDeclarationReference;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.cxxelement.DestructorDeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest30TypeDef extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 5, (Object) references.size());

        DeclarationReference myArrayDestrRef = references.get(1);
        Assert.assertTrue(myArrayDestrRef instanceof DestructorDeclarationReference);
        assertDeclaration(getRequiredDependency(myArrayDestrRef).getDeclaration(), "MyArray.h", "MyArray", 27);

        DeclarationReference myStringRef = references.get(2);
        assertDeclaration(getRequiredDependency(myStringRef).getDeclaration(), "MyString.h", "MyString", 44);

        DeclarationReference myArrayConstrRef = references.get(4);
        Assert.assertTrue(myArrayConstrRef instanceof ConstructorDeclarationReference);
        assertDeclaration(getRequiredDependency(myArrayConstrRef).getDeclaration(), "MyArray.h", "MyArray", 27);
    }
}
