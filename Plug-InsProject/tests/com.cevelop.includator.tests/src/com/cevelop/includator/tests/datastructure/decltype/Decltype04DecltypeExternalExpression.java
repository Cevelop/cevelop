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


public class Decltype04DecltypeExternalExpression extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 6, (Object) references.size());

        assertDeclRefName("main", references.get(0));
        assertDeclRefName("~Outer", references.get(1)); //Dtor
        assertDeclRefName("type Inner of expression 'i'", references.get(2));
        assertDeclRefName("i", references.get(3));
        assertDeclRefName("Outer", references.get(4)); //Type
        assertDeclRefName("o", references.get(5));
    }
}
