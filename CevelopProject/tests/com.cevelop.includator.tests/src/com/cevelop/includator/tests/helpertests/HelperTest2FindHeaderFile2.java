/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpertests;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest2FindHeaderFile2 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        IncludatorFile headerFile = FileHelper.getNameCorrelatingHeaderFile(getActiveIncludatorFile());
        Assert.assertNull(headerFile);
    }
}
