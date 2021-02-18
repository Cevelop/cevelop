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
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest48FunctionTemplateReference extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 18, (Object) references.size());

        DeclarationReference nothingOpDefinitionRef = references.get(11);
        Assert.assertEquals("nothingOp", nothingOpDefinitionRef.getName());

        DeclarationReferenceDependency nothingOpDefinitionDependency = getRequiredDependency(nothingOpDefinitionRef);
        Assert.assertTrue(nothingOpDefinitionDependency.isLocalDependency());
        Assert.assertTrue(nothingOpDefinitionDependency.isSelfDependency());

        DeclarationReference nothingOpTemplateInstanceRef = references.get(17);
        DeclarationReferenceDependency nothingOpTemplateInstanceDependency = getRequiredDependency(nothingOpTemplateInstanceRef);
        Assert.assertTrue(nothingOpTemplateInstanceDependency.isLocalDependency());
        Assert.assertFalse(nothingOpTemplateInstanceDependency.isSelfDependency());
        assertDeclaration(nothingOpTemplateInstanceDependency.getDeclaration(), "main.cpp", "nothingOp", 218);
    }
}
