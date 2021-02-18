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


public class ConstructorRefTest3 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 11, (Object) references.size());
        DeclarationReference constructorRef = references.get(2);
        assertDeclRefName("X", constructorRef);
        assertNodeSignature("UseX", constructorRef.getASTNode());
        assertDeclaration(getRequiredDependency(constructorRef).getDeclaration(), "X.h", "X", 20);
    }
}
