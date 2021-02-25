/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpertests;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest7FileHelper extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        IFile existingFile = getCurrentIFile("A.cpp");
        Assert.assertNotNull(existingFile);
        IFile inexistingFile = getCurrentProject().getFile("Crap.cpp");
        Assert.assertNotNull(inexistingFile);
        Assert.assertFalse(inexistingFile.exists());
        IPath notInProjectPath = getCurrentProject().getLocation().uptoSegment(0).append("somefile.h");
        Assert.assertNull(FileHelper.getIFile(FileHelper.pathToUri(notInProjectPath)));
    }
}
