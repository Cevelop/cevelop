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


/**
 * Test created for bug report #60 to assure correct resolution of references to static member functions.
 */
public class DataStructureReferenceTest32StaticMemberFunctionReference extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 3, (Object) references.size());

        DeclarationReference reference = references.get(1);
        assertDeclRefName("nodeID", reference);

        DeclarationReferenceDependency dependency = getRequiredDependency(reference);
        Assert.assertFalse(dependency.isLocalDependency());
        assertDeclRefDependencyTargetFile("A.h", reference.getRequiredDependencies());
        assertDeclaration(dependency.getDeclaration(), "A.h", "nodeID", 39);
    }
}
