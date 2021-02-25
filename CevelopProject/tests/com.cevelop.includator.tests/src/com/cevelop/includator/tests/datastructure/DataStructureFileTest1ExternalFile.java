/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureFileTest1ExternalFile extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("externalFrameworkTest");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        String path = externalTestResourcesHolder.makeProjectAbsolutePath("externalFrameworkTest/QWER.h").toOSString();
        IncludatorFile externalFile = getActiveProject().getFile(path);
        Assert.assertEquals(path, externalFile.getFilePath());
        Assert.assertNotNull(externalFile.getTranslationUnit());
    }
}
