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

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ConstructorRefTest8parameterDeclarationNoImpl extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 8, (Object) references.size());
        DeclarationReference copyConstructorRef = references.get(2);
        assertDeclRefName("X", copyConstructorRef);
        assertNodeSignature("x", copyConstructorRef.getASTNode());
        assertDeclaration(getRequiredDependency(copyConstructorRef).getDeclaration(), "X.h", "X", 6);
    }
}
