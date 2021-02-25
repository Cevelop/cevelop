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

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest28ConstructorResolutionTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 7, (Object) references.size());

        DeclarationReference classXReference = references.get(2);
        assertDeclaration(getRequiredDependency(classXReference).getDeclaration(), "X.h", "X", 6);

        DeclarationReference constructor1Reference = references.get(4);
        assertDeclaration(getRequiredDependency(constructor1Reference).getDeclaration(), "X.h", "X", 19);

        DeclarationReference constructor2Reference = references.get(6);
        assertDeclaration(getRequiredDependency(constructor2Reference).getDeclaration(), "X.h", "X", 25);
    }
}
