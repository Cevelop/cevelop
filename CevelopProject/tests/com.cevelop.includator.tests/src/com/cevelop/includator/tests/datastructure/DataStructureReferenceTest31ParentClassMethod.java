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
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest31ParentClassMethod extends IncludatorTest {

    //TODO Implement Test, Fix Associated Functionality
    //@Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 4, (Object) references.size());

        DeclarationReference myStringRef = references.get(1);
        assertDeclaration(getRequiredDependency(myStringRef).getDeclaration(), "MyString.h", "MyString", 44);

        DeclarationReference myArrayConstrRef = references.get(3);
        Assert.assertTrue(myArrayConstrRef instanceof ConstructorDeclarationReference);
        assertDeclaration(getRequiredDependency(myArrayConstrRef).getDeclaration(), "MyArray.h", "MyArray", 27);
    }

    @Test
    public void dummyPlaceholder() throws Throwable {}
}
