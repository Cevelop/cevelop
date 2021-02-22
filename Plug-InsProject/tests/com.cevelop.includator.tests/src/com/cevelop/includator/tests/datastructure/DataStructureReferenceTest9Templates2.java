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


public class DataStructureReferenceTest9Templates2 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 25, (Object) references.size());

        DeclarationReference helloCall = references.get(23);
        List<DeclarationReferenceDependency> dependencies = helloCall.getRequiredDependencies();
        assertDeclarationReference("hello", "main.cpp", 294, helloCall);

        Assert.assertEquals((Object) 3, (Object) dependencies.size());
        Assert.assertEquals("My2Hello.hpp", getDeclaraionPath(dependencies.get(0)));
        Assert.assertEquals("My3Hello.hpp", getDeclaraionPath(dependencies.get(1)));
        Assert.assertEquals("MyHello.hpp", getDeclaraionPath(dependencies.get(2)));
    }

    private String getDeclaraionPath(final DeclarationReferenceDependency dependency) {
        return dependency.getDeclaration().getFile().getProjectRelativePath();
    }
}
