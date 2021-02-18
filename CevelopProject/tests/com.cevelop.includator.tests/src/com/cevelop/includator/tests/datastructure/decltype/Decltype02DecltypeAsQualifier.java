/*******************************************************************************
 * Copyright (c) 2014 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Thomas Corbat (IFS) - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.decltype;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class Decltype02DecltypeAsQualifier extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 11, (Object) references.size());

        assertDeclRefName("Inner", references.get(0)); //Type
        assertDeclRefName("Inner", references.get(1)); //Ctor
        assertDeclRefName("Inner", references.get(2)); //Copy Ctor
        assertDeclRefName("~Inner", references.get(3)); //Dtor
        assertDeclRefName("Outer", references.get(4)); //Type
        assertDeclRefName("Outer", references.get(5)); //Ctor
        assertDeclRefName("Outer", references.get(6)); //Copy Ctor
        assertDeclRefName("~Outer", references.get(7)); //Dtor

        assertDeclRefName("main", references.get(8));
        assertDeclRefName("i", references.get(9));
        assertDeclRefName("o", references.get(10));

    }
}
