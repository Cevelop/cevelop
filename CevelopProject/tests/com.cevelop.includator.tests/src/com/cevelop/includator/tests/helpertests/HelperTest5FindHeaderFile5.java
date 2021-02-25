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
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest5FindHeaderFile5 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        boolean caught = false;
        try {
            // causes IncludatorException caused by inclusion of unknown file extension.
            FileHelper.getNameCorrelatingHeaderFile(getActiveIncludatorFile());
        } catch (IncludatorException e) {
            String msg = "CDT does not consider the file 'A.xy' as a C or C++ source or header file. " +
                         "Please add '*.xy' to the 'File Types' list under 'C/C++ General->File Types' in 'Eclipse Preferences' or the project's 'Project Properties'.";
            Assert.assertEquals(msg, e.getMessage());
            caught = true;
        }
        Assert.assertTrue(caught);
    }
}
