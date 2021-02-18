/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.declarationstoretests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.Declaration;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DeclarationStore1PutGetTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> refsInAcpp = getActiveFileDeclarationReferences();
        Assert.assertEquals((Object) 5, (Object) refsInAcpp.size());
        Declaration firstDecl = getRequiredDependency(refsInAcpp.get(2)).getDeclaration();

        List<DeclarationReference> refsInBcpp = getDeclarationReferences("B.cpp");
        Assert.assertEquals((Object) 5, (Object) refsInBcpp.size());
        Declaration secondDecl = getRequiredDependency(refsInBcpp.get(2)).getDeclaration();
        Assert.assertEquals(firstDecl, secondDecl);
    }
}
