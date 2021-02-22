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


public class MultipleDefinitionTest1TypeDef extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 3, (Object) references.size());
        DeclarationReference sizeTRef = references.get(1);
        assertDeclRefDependencyTargetFile("a.h", sizeTRef.getRequiredDependencies());
        assertStatus("Found multiple definitions of size_t in main.cpp[2:3,2:9]. Prefering size_t in a.h[1:27,1:33]. Candidates are:",
                "a.h[1:27,1:33]", "b.h[1:27,1:33]");
    }
}
