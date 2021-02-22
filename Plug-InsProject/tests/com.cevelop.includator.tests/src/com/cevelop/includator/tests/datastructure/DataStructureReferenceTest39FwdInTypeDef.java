/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
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
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest39FwdInTypeDef extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 3, (Object) references.size());

        DeclarationReference myARef = references.get(1);
        assertDeclRefName("MyA", myARef);
        DeclarationReferenceDependency myADependency = getRequiredDependency(myARef);
        Assert.assertTrue(myADependency.isLocalDependency());

        DeclarationReference aRef = references.get(0);
        assertDeclRefName("A", aRef);

        DeclarationReferenceDependency aDependency = getRequiredDependency(aRef);
        Assert.assertTrue(aDependency.isLocalDependency());
    }
}
