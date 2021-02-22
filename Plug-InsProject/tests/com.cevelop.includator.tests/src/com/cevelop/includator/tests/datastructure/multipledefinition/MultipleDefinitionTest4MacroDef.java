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


public class MultipleDefinitionTest4MacroDef extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 3, (Object) references.size());
        DeclarationReference xyMacroRef = references.get(1);
        assertDeclRefDependencyTargetFile("b.h", xyMacroRef.getRequiredDependencies());
        String mgs = "There are several potential macro definitions for XY in main.cpp[2:3,2:5]. Assuming b.h[1:9,1:11] as correct. Candidates were:";
        assertStatus(mgs, "b.h[1:9,1:11]", "a.h[1:9,1:11]");
    }
}
