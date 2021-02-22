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


public class IndexAdaptionTest8WrongFileTypesNoExtension extends IncludatorTest {

    private static final String EXTERNAL_FOLDER_NAME = "IndexAdaptionTest8WrongFileTypesNoExtension";

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
        assertNonCFile(new Path(EXTERNAL_FOLDER_NAME).append("someCSourceWithoutExtension"), "someCSourceWithoutExtension");

        String expected = "File 'someCSourceWithoutExtension' has unknown file type and was thus ignored. If it is a C / C++ file, please add " +
                          "'someCSourceWithoutExtension' to the 'File Types' list under 'C/C++ General->File Types' in 'Eclipse Preferences' or in the project's 'Project Properties'.";
        assertStatus(expected);
    }
}
