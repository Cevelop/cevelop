/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.returnstatustests;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.junit.Test;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.findunusedincludes.FindUnusedIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ReturnStatus2ExceptionStatus extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        // openActiveFileInEditor(); //not opening the editor will result in an exception since there will not be any open editor.
        MultiStatus status = runAlgorithmsAsAction(new FindUnusedIncludesAlgorithm(), AlgorithmScope.EDITOR_SCOPE);
        String expectedStatusStr =
                                 "Status ERROR: com.cevelop.includator code=0 Includator Static Include Analysis Status null children=[Status ERROR: com.cevelop.includator code=0 Unable to find the active file com.cevelop.includator.helpers.IncludatorException: Unable to find the active file]";
        assertStatus(1, IStatus.ERROR, expectedStatusStr, status);
    }
}
