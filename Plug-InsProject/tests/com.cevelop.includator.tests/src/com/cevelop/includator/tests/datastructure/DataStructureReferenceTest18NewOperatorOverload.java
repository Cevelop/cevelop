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


public class DataStructureReferenceTest18NewOperatorOverload extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 5, (Object) references.size());

        DeclarationReference newOperator = references.get(3);
        Assert.assertFalse(newOperator.hadProblemsWhileResolving());
        Assert.assertEquals("operator new", newOperator.getASTNode().toString());
        DeclarationReferenceDependency operatorNewDependency = getRequiredDependency(newOperator);
        assertDeclaration(operatorNewDependency.getDeclaration(), "MyClass.h", "operator new", 66);
    }
}
