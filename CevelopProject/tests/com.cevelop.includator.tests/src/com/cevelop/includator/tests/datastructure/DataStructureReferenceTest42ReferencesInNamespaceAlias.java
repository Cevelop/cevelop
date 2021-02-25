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
import com.cevelop.includator.cxxelement.UsingDeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest42ReferencesInNamespaceAlias extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 4, (Object) references.size());

        DeclarationReference usingFooRef = references.get(1);
        Assert.assertTrue(usingFooRef instanceof UsingDeclarationReference);
        assertDeclaration(getRequiredDependency(usingFooRef).getDeclaration(), "namespace.h", "foo", 38);
        Assert.assertTrue(usingFooRef.isForwardDeclarationEnough());

        DeclarationReference fooRef = references.get(3);
        assertDeclaration(getRequiredDependency(fooRef).getDeclaration(), "main.cpp", "foo", 55);
        Assert.assertTrue(fooRef.isForwardDeclarationEnough());
    }
}
