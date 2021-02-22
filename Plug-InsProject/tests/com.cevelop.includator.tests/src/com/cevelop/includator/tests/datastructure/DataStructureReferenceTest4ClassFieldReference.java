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


public class DataStructureReferenceTest4ClassFieldReference extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 6, (Object) references.size());

        DeclarationReference reference = references.get(5);
        assertDeclRefName("count", reference);

        DeclarationReferenceDependency dependency = getRequiredDependency(reference);
        Assert.assertFalse(dependency.isLocalDependency());
        assertDeclRefDependencyTargetFile("C.h", reference.getRequiredDependencies());

        assertDeclaration(dependency.getDeclaration(), "C.h", "count", 24);
    }
}
