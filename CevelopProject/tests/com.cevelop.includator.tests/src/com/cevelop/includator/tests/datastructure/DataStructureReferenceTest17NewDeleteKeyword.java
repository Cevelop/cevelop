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


public class DataStructureReferenceTest17NewDeleteKeyword extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 7, (Object) references.size());

        DeclarationReference operatorNewRef = references.get(3);
        Assert.assertFalse(operatorNewRef.hadProblemsWhileResolving());
        DeclarationReferenceDependency newOperatorDependency = getRequiredDependency(operatorNewRef);
        assertDeclaration(newOperatorDependency.getDeclaration(), "A.h", "operator new", 60);

        DeclarationReference destrRef = references.get(5);
        assertDeclRefName("~A", destrRef);
        Assert.assertFalse(destrRef.hadProblemsWhileResolving());
        DeclarationReferenceDependency destructorDependency = getRequiredDependency(destrRef);
        assertDeclaration(destructorDependency.getDeclaration(), "A.h", "A", 40);

        DeclarationReference operatorDeleteRef = references.get(6);
        Assert.assertFalse(operatorDeleteRef.hadProblemsWhileResolving());
        DeclarationReferenceDependency deleteOperatorDependency = getRequiredDependency(operatorDeleteRef);
        assertDeclaration(deleteOperatorDependency.getDeclaration(), "A.h", "operator delete", 89);
    }
}
