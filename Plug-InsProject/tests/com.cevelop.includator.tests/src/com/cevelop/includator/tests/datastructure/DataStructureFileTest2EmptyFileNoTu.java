/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure;

import java.util.Collection;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureFileTest2EmptyFileNoTu extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("emptyFileTest");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        Collection<IncludatorFile> files = getActiveProject().getAffectedFiles();
        Assert.assertEquals((Object) 1, (Object) files.size());
        IncludatorFile file = files.iterator().next();
        Assert.assertEquals("A.cpp", file.getProjectRelativePath());
        Assert.assertNotNull(file.getTranslationUnit());

        String path = externalTestResourcesHolder.makeProjectAbsolutePath("emptyFileTest/empty.h").toOSString();
        // FIXME: This is a band-aid. This should not be here. We need to figure out a generic way of ensuring that external resources are external.
        // SOB has had some issues with external test resources NOT being eclipse projects. Talk to him when fixing this!
        externalTestResourcesHolder.getProject().close(new NullProgressMonitor());
        IncludatorFile externalFile = getActiveProject().getFile(path);
        Assert.assertNull(externalFile.getTranslationUnit());

        assertStatus("Failed to load translation for file '" + path + "'.");
    }
}
