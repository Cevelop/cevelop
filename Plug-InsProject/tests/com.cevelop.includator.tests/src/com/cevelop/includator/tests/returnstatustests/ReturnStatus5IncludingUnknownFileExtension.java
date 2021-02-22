/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.returnstatustests;

import org.eclipse.core.runtime.MultiStatus;
import org.junit.Test;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ReturnStatus5IncludingUnknownFileExtension extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        MultiStatus status = runAlgorithmsAsAction(new OrganizeIncludesAlgorithm(), AlgorithmScope.FILE_SCOPE);

        String msg = "CDT does not consider the file 'stupidExtension.crap' as a C or C++ source or header file. " +
                     "Please add '*.crap' to the 'File Types' list under 'C/C++ General->File Types' in 'Eclipse Preferences' or the project's 'Project Properties'.";
        assertStatus(status, msg);
    }
}
