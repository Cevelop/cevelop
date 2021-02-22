/*******************************************************************************
 * Copyright (c) 2014 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Thomas Corbat (IFS) - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.auto;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class Auto02AutoForExternalExpression extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 5, (Object) references.size());

        assertDeclRefName("main", references.get(0));
        assertDeclRefName("otherS", references.get(1));
        assertDeclRefName("S", references.get(2));
        assertDeclRefName("type S of expression 's'", references.get(3));
        assertDeclRefName("s", references.get(4));
    }
}
