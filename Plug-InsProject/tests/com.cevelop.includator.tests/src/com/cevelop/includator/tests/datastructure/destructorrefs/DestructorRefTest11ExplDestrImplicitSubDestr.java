/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.destructorrefs;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.cxxelement.DestructorDeclarationReference;
import com.cevelop.includator.cxxelement.ImplicitDestructorDeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DestructorRefTest11ExplDestrImplicitSubDestr extends IncludatorTest {

    private int    expectedRefTargetOffset;
    private String expectedTargetName;

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 3, (Object) references.size());

        DestructorDeclarationReference zDestrRef = getDestructorReference(references.get(1));
        Assert.assertTrue(zDestrRef instanceof ImplicitDestructorDeclarationReference);
        assertDeclRefName("~Z", zDestrRef);
        assertNodeSignature("~Y", zDestrRef.getASTNode());
        assertDeclaration(getRequiredDependency(zDestrRef).getDeclaration(), "Z.h", expectedTargetName, expectedRefTargetOffset);
    }

    @Override
    protected void configureTest(Properties properties) {
        expectedRefTargetOffset = Integer.parseInt(properties.getProperty("expectedRefTargetOffset"));
        expectedTargetName = properties.getProperty("expectedTargetName");
        super.configureTest(properties);
    }

}
