/*******************************************************************************
 * Copyright (c) 2010, 2014 Institute for Software, HSR Hochschule fuer Technik
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


public class DataStructureReferenceTest7Macros extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 3, (Object) references.size());

        DeclarationReference minRef = references.get(2);
        assertDeclRefName("min", minRef);

        Assert.assertEquals((Object) 1, (Object) minRef.getRequiredDependencies().size());
        assertDeclRefDependencyTargetFile("B.h", minRef.getRequiredDependencies());
        assertDeclaration(getRequiredDependency(minRef).getDeclaration(), "B.h", "min", 8);
    }
}
