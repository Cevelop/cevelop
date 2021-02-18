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


public class MultipleDefinitionTest5Type extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 5, (Object) references.size());

        DeclarationReference classXRef = references.get(2);
        Assert.assertTrue(classXRef.isClassReference());
        String expectedFileName = "a.h";
        assertDeclRefDependencyTargetFile(expectedFileName, classXRef.getRequiredDependencies());
        assertStatus("Found multiple definitions of X in main.cpp[2:5,2:6]. Prefering X in a.h[1:7,1:8]. Candidates are:", "a.h[1:7,1:8]",
                "b.h[1:7,1:8]");

        DeclarationReference ctorRef = getConstructorReference(references.get(4));
        assertDeclRefDependencyTargetFile(expectedFileName, ctorRef.getRequiredDependencies());
        assertStatus("Found multiple definitions of X in main.cpp[2:7,2:8]. Prefering X in a.h[1:7,1:8]. Candidates are:", "b.h[1:7,1:8]",
                "a.h[1:7,1:8]");

        DeclarationReference dtorRef = getDestructorReference(references.get(1));
        assertDeclRefDependencyTargetFile(expectedFileName, dtorRef.getRequiredDependencies());
        assertStatus("Found multiple definitions of ~X in main.cpp[2:7,2:8]. Prefering X in a.h[1:7,1:8]. Candidates are:", "b.h[1:7,1:8]",
                "a.h[1:7,1:8]");
    }
}
