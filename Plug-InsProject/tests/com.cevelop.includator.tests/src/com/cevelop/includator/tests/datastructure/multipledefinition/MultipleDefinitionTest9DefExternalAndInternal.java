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


public class MultipleDefinitionTest9DefExternalAndInternal extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("MultipleDefinitionTest9DefExternalAndInternal");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals(3, references.size());
        assertDeclRefDependencyTargetFile("i1.h", references.get(1).getRequiredDependencies());

        String problemMsg = "Found multiple definitions of I in main.cpp[2:3,2:4]. Prefering I in i1.h[1:13,1:14]. Candidates are:";
        String candidate1 = "i1.h[1:13,1:14]";
        String candidate2 = externalTestResourcesHolder.makeProjectAbsolutePath("MultipleDefinitionTest9DefExternalAndInternal/i2.h").append(
                "/[12,13]").toOSString();
        assertStatus(problemMsg, candidate1, candidate2);
    }
}
