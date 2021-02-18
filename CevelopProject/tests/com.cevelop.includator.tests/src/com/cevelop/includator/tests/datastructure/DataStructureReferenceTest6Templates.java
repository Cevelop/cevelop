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
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest6Templates extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 10, (Object) references.size());

        assertDeclRefName("Pair<My>", references.get(2));
        assertDeclRefName("My", references.get(3));
        assertDeclRefName("first_", references.get(9));

        assertDeclRefDependencyTargetFile("B.hpp", references.get(2).getRequiredDependencies());
        assertDeclRefDependencyTargetFile("C.hpp", references.get(3).getRequiredDependencies());
        assertDeclRefDependencyTargetFile("B.hpp", references.get(9).getRequiredDependencies());
    }
}
