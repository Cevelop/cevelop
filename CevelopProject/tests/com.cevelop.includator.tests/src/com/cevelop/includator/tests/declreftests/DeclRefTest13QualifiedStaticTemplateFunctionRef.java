/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.declreftests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DeclRefTest13QualifiedStaticTemplateFunctionRef extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 12, (Object) references.size());

        DeclarationReference barRef = references.get(11);
        Assert.assertEquals("bar", barRef.getName());

        List<DeclarationReferenceDependency> dependencies = barRef.getRequiredDependencies();
        Assert.assertEquals((Object) 1, (Object) dependencies.size());

        DeclarationReferenceDependency dependency = dependencies.get(0);
        Assert.assertTrue(dependency.isLocalDependency());
        Assert.assertFalse(dependency.isSelfDependency());
    }
}
