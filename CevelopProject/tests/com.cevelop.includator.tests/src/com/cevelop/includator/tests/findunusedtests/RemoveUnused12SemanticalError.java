/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.findunusedtests;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.findunusedincludes.FindUnusedIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class RemoveUnused12SemanticalError extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        MultiStatus status = runAlgorithmsAsAction(new FindUnusedIncludesAlgorithm(), AlgorithmScope.FILE_SCOPE);
        Assert.assertEquals((Object) 1, (Object) status.getChildren().length);
        IStatus childStatus = status.getChildren()[0];
        String expectedChildMsg = "No declaration found for \"X\" in file A.cpp[2:5,2:6].";
        assertStatus(IStatus.WARNING, expectedChildMsg, childStatus);
    }
}
