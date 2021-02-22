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
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest37TypedefToClassFwdDecl extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 6, (Object) references.size());

        DeclarationReference fwdDeclaration = references.get(0);
        assertDeclaration(getRequiredDependency(fwdDeclaration).getDeclaration(), "src/main.cpp", "Templ", 6);
        Assert.assertTrue(fwdDeclaration.isForwardDeclarationEnough());

        DeclarationReference typedefT = references.get(1);
        assertDeclaration(getRequiredDependency(typedefT).getDeclaration(), "src/main.cpp", "T", 28);
        Assert.assertTrue(!typedefT.isForwardDeclarationEnough());
    }
}
