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


public class DataStructureReferenceTest25CassImplToClassDecl extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 2, (Object) references.size());

        DeclarationReference fooReference = references.get(0);
        assertDeclRefName("foo", fooReference);
        DeclarationReferenceDependency dependency = getRequiredDependency(fooReference);
        assertDeclaration(dependency.getDeclaration(), "A.h", "foo", 25);

        DeclarationReference classReference = references.get(1);
        assertDeclRefName("A", classReference);
        List<DeclarationReferenceDependency> classDependencies = classReference.getRequiredDependencies();
        assertDeclRefDependencyTargetFile("A.h", classDependencies);
    }
}
