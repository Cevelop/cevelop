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

import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.helpers.MainFinderHelper;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest12MainFinderTest1 extends IncludatorTest {

    private boolean shouldFindMain;

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("externalFrameworkTest");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        IASTFunctionDefinition main = MainFinderHelper.findMain(getActiveProject());
        Assert.assertEquals(shouldFindMain, main != null);
    }

    @Override
    protected void configureTest(Properties properties) {
        shouldFindMain = Boolean.parseBoolean(properties.getProperty("shouldFindMain"));
        super.configureTest(properties);
    }

}
