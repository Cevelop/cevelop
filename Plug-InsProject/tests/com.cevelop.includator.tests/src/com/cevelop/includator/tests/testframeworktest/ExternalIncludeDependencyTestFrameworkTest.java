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

import org.eclipse.cdt.core.model.IIncludeReference;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ExternalIncludeDependencyTestFrameworkTest extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("externalFrameworkTest");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        IIncludeReference[] inc = getActiveProject().getIncludeReferences();
        String actualPathString = FileHelper.pathToStringPath(inc[0].getPath()); // cannot use URI here. see
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=331086
        assertExternalIncludatorResourcePath("externalFrameworkTest", actualPathString);
        List<DeclarationReference> references = getActiveFileDeclarationReferences();
        Assert.assertEquals((Object) 5, (Object) references.size());
        DeclarationReferenceDependency externalDependency = getRequiredDependency(references.get(2));
        assertExternalIncludatorResourcePath("externalFrameworkTest/QWER.h", externalDependency.getDeclaration().getFileLocation().getFileName());
    }
}
