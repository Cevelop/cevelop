/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.constructorrefs;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.ConstructorDeclarationReference;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ConstructorRefTest11ExplConstrExplSubConstr extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 8, (Object) references.size());

        ConstructorDeclarationReference zConstrRef = getConstructorReference(references.get(3));
        Assert.assertTrue(zConstrRef.isDefaultConstructor());
        assertDeclRefName("Z", zConstrRef);
        assertDeclaration(getRequiredDependency(zConstrRef).getDeclaration(), "Z.h", "Z", 20);

        ConstructorDeclarationReference zCopyConstrRef = getConstructorReference(references.get(6));
        Assert.assertTrue(zCopyConstrRef.isCopyConstructor());
        assertDeclRefName("Z", zCopyConstrRef);
        assertDeclaration(getRequiredDependency(zCopyConstrRef).getDeclaration(), "Z.h", "Z", 30);
    }
}
