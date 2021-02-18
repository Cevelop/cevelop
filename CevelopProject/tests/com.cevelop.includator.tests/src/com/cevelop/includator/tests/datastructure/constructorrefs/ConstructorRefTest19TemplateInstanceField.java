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
import com.cevelop.includator.cxxelement.SpecialMemberFunctionDeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ConstructorRefTest19TemplateInstanceField extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 9, (Object) references.size());

        SpecialMemberFunctionDeclarationReference aConstrRef = getConstructorReference(references.get(2));
        Assert.assertTrue(aConstrRef.isOnlyReferenceName());
        assertDeclarationReference("A", "B.cpp", 22, aConstrRef);
        assertNodeSignature("B", aConstrRef.getASTNode());// the helper name is the constructor definition of B
        assertDeclaration(getRequiredDependency(aConstrRef).getDeclaration(), "A.h", "A", 41);
    }

}
