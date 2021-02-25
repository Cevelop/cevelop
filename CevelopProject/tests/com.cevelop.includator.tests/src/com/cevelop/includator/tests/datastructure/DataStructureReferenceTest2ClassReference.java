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


public class DataStructureReferenceTest2ClassReference extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 5, (Object) references.size());

        DeclarationReference classReference = references.get(2);
        assertDeclRefName("C", classReference);

        DeclarationReferenceDependency dependency = getRequiredDependency(classReference);
        Assert.assertFalse(dependency.isLocalDependency());
        assertDeclRefDependencyTargetFile("C.h", classReference.getRequiredDependencies());

        IName name = dependency.getDeclaration().getName();
        Assert.assertEquals("C", name.toString());
        assertFileLocation(name.getFileLocation(), "C.h", 6, 1);
    }
}
