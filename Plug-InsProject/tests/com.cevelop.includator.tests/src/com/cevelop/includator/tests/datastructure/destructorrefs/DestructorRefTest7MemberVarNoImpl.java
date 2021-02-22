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
import com.cevelop.includator.tests.base.IncludatorTest;


public class DestructorRefTest7MemberVarNoImpl extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 9, (Object) references.size());

        DeclarationReference useXDestructorRef = references.get(5);
        assertDeclRefName("~UseX", useXDestructorRef);
        assertNodeSignature("UseX", useXDestructorRef.getASTNode());
        assertDeclaration(getRequiredDependency(useXDestructorRef).getDeclaration(), "A.cpp", "UseX", 21);

        DeclarationReference xDestructorRef = references.get(6);
        assertDeclRefName("~X", xDestructorRef);
        assertNodeSignature("UseX", xDestructorRef.getASTNode());
        assertDeclaration(getRequiredDependency(xDestructorRef).getDeclaration(), "X.h", "X", 6);
    }
}
