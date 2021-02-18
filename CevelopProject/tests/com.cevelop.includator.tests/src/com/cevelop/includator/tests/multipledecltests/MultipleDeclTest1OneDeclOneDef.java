/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.multipledecltests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.tests.base.IncludatorTest;


public class MultipleDeclTest1OneDeclOneDef extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 6, (Object) references.size());

        DeclarationReference classBRef = references.get(0);
        assertDeclarationReference("B", "A.cpp", 6, classBRef);

        List<DeclarationReferenceDependency> dependencies = classBRef.getAllDependencies();
        Assert.assertEquals((Object) 2, (Object) dependencies.size());
        assertDeclaration(dependencies.get(0).getDeclaration(), "A.cpp", "B", 6);
        assertDeclaration(dependencies.get(1).getDeclaration(), "A.cpp", "B", 20);
    }
}
