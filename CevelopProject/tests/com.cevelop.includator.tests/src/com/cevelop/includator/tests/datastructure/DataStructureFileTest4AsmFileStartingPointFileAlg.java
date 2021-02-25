/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.findunusedincludes.FindUnusedIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureFileTest4AsmFileStartingPointFileAlg extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        Assert.assertNull(getActiveIncludatorFile()); // active file is .asm file
        MultiStatus status = runAlgorithmsAsAction(new FindUnusedIncludesAlgorithm(), AlgorithmScope.FILE_SCOPE);
        Assert.assertEquals((Object) 1, (Object) status.getChildren().length);
        String expectedMsg = "The algorithm your trying to run must be run on a C/C++ source or header file which is currently not the case.";
        assertStatus(IStatus.ERROR, expectedMsg, status.getChildren()[0]);
    }
}
