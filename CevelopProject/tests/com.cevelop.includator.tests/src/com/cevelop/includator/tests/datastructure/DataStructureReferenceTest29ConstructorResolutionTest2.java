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


public class DataStructureReferenceTest29ConstructorResolutionTest2 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        // TODO: "Was adapted due to missing references caused by cdt bugs 351547 and 354585. indizes need to be re-adapted when swichting to next cdt
        // release.

        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 8, (Object) references.size());

        DeclarationReference classXReference = references.get(1);
        assertDeclaration(getRequiredDependency(classXReference).getDeclaration(), "X.h", "X", 6);

        DeclarationReference constructor0Reference = references.get(3);
        assertDeclaration(getRequiredDependency(constructor0Reference).getDeclaration(), "X.h", "X", 19);

        DeclarationReference constructor1Reference = references.get(6);
        assertDeclaration(getRequiredDependency(constructor1Reference).getDeclaration(), "X.h", "X", 25);

        DeclarationReference destructorReference = references.get(7);
        assertDeclaration(getRequiredDependency(destructorReference).getDeclaration(), "X.h", "~X", 44);
    }
}
