/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpertests;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest3FindHeaderFile3 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        IncludatorFile headerFile = FileHelper.getNameCorrelatingHeaderFile(getActiveIncludatorFile());
        Assert.assertEquals("X" + File.separator + "A.h", headerFile.getProjectRelativePath());
    }
}
