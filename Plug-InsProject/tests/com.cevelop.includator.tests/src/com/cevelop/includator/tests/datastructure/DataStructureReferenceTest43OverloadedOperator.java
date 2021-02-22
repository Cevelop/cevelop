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
import com.cevelop.includator.cxxelement.MethodDeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest43OverloadedOperator extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 9, (Object) references.size());

        DeclarationReference operatorPlus = references.get(8);
        Assert.assertTrue(operatorPlus instanceof MethodDeclarationReference);
        assertDeclaration(getRequiredDependency(operatorPlus).getDeclaration(), "src/rational.h", "operator +", 57);
        Assert.assertFalse(operatorPlus.isForwardDeclarationEnough());
    }
}
