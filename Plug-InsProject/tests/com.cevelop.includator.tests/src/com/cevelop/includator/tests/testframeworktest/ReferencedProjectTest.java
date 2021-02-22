/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.testframeworktest;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IIncludeReference;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ReferencedProjectTest extends IncludatorTest {

    private static final String REFERENCED_PROJECT_NAME1 = "otherProject1";
    private static final String REFERENCED_PROJECT_NAME2 = "otherProject2";

    @Override
    protected void initReferencedProjects() throws Exception {
        stageReferencedProjectForBothProjects(REFERENCED_PROJECT_NAME1, "ReferencedProjectTest_p2.rts");
        stageReferencedProjectForBothProjects(REFERENCED_PROJECT_NAME2, "ReferencedProjectTest_p3.rts");
        super.initReferencedProjects();
    }

    @Test
    public void runTest() throws Throwable {
        List<ICProject> referencedProjects = currentProjectHolder.getReferencedProjects();
        Assert.assertEquals("otherProject1, otherProject2", referencedProjects.stream().map(ICProject::toString).collect(Collectors.joining(", ")));
        IIncludeReference[] inc = getActiveProject().getIncludeReferences();
        Assert.assertEquals(2, inc.length);

        Assert.assertEquals(referencedProjects.get(0).getProject().getLocation(), inc[0].getPath());
        Assert.assertEquals(referencedProjects.get(1).getProject().getLocation(), inc[1].getPath());

        List<DeclarationReference> refs = getActiveFileDeclarationReferences();
        Assert.assertEquals(5, refs.size());
        List<DeclarationReferenceDependency> requiredProj1Dependencies = refs.get(1).getRequiredDependencies();
        Assert.assertEquals(1, requiredProj1Dependencies.size());
        Assert.assertTrue(requiredProj1Dependencies.get(0).getDeclaration().getFile().getFilePath().endsWith(REFERENCED_PROJECT_NAME1 +
                                                                                                             FileHelper.PATH_SEGMENT_SEPARATOR +
                                                                                                             "A.h"));
        List<DeclarationReferenceDependency> requiredProj2Dependencies = refs.get(3).getRequiredDependencies();
        Assert.assertEquals(1, requiredProj2Dependencies.size());
        Assert.assertTrue(requiredProj2Dependencies.get(0).getDeclaration().getFile().getFilePath().endsWith(REFERENCED_PROJECT_NAME2 +
                                                                                                             FileHelper.PATH_SEGMENT_SEPARATOR +
                                                                                                             "B.h"));
    }
}
