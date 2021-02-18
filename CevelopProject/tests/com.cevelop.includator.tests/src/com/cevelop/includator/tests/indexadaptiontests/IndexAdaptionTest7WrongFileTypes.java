/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.indexadaptiontests;

import org.eclipse.core.runtime.Path;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.tests.base.IncludatorTest;


public class IndexAdaptionTest7WrongFileTypes extends IncludatorTest {

    private static final String EXTERNAL_FOLDER_NAME = "IndexAdaptionTest7WrongFileTypes";

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects(EXTERNAL_FOLDER_NAME);
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        Assert.assertNotNull(getExternalIndexFile(new Path(EXTERNAL_FOLDER_NAME).append("someCSource.cpp")));
        assertNonCFile(new Path(EXTERNAL_FOLDER_NAME).append("README.txt"), "*.txt");
        assertNonCFile(new Path(EXTERNAL_FOLDER_NAME).append("White.png"), "*.png");
    }
}
