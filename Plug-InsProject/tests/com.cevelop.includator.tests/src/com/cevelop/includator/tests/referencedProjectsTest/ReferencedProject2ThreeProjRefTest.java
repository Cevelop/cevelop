/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.referencedProjectsTest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ReferencedProject2ThreeProjRefTest extends IncludatorTest {

    @Override
    protected void initReferencedProjects() throws Exception {
        stageReferencedProjectForBothProjects("otherProject1", "ReferencedProject2ThreeProjRefTest_p2.rts");
        stageReferencedProjectForBothProjects("otherProject2", "ReferencedProject2ThreeProjRefTest_p3.rts");
        super.initReferencedProjects();
    }

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();
        Assert.assertEquals((Object) 5, (Object) references.size());
        assertDeclRefDependencyTargetFile("A.h", references.get(1).getRequiredDependencies());
        assertDeclRefDependencyTargetFile("B.h", references.get(3).getRequiredDependencies());
    }
}
