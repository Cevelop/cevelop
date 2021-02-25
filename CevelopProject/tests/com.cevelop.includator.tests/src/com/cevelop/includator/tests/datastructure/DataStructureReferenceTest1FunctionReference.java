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

import org.eclipse.cdt.core.dom.IName;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest1FunctionReference extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 2, (Object) references.size());

        DeclarationReference fooReference = references.get(1);
        assertDeclRefName("foo", fooReference);

        DeclarationReferenceDependency dependency = getRequiredDependency(fooReference);
        Assert.assertFalse(dependency.isLocalDependency());
        assertDeclRefDependencyTargetFile("C.h", fooReference.getRequiredDependencies());

        IName name = dependency.getDeclaration().getName();
        Assert.assertEquals("foo", name.toString());
        assertFileLocation(name.getFileLocation(), "C.h", 4, 3);
    }
}
