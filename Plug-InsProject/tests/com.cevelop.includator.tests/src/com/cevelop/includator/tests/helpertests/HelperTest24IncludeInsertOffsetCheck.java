/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpertests;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.helpers.offsetprovider.InsertIncludeOffsetProvider;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest24IncludeInsertOffsetCheck extends IncludatorTest {

    private int     expectedOffset;
    private boolean insertSystemInclude;

    @Test
    public void runTest() throws Throwable {
        IncludatorFile file = getActiveIncludatorFile();
        Assert.assertEquals((Object) adaptExpectedOffset(file.getIFile(), expectedOffset), (Object) new InsertIncludeOffsetProvider(file
                .getTranslationUnit(), insertSystemInclude).getInsertOffset());
    }

    @Override
    protected void configureTest(Properties properties) {
        expectedOffset = Integer.parseInt(properties.getProperty("offset"));
        insertSystemInclude = Boolean.parseBoolean(properties.getProperty("insertSystemInclude", "false"));
        super.configureTest(properties);
    }
}
