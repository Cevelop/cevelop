/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.multipledefinition;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class MultipleDefinitionTest7DefInCurrentSourceAndInHeader extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 6, (Object) references.size());
        DeclarationReference classXRef = references.get(0);
        Assert.assertTrue(classXRef.isClassReference());
        assertDeclRefDependencyTargetFile("main.cpp", classXRef.getRequiredDependencies());
        assertStatus("Found multiple definitions of X in main.cpp[2:7,2:8]. Prefering X in main.cpp[2:7,2:8]. Candidates are:", "X.h[1:7,1:8]",
                "main.cpp[2:7,2:8]");
    }
}
