/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.returnstatustests;

import org.junit.Test;

import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ReturnStatus4DuplicateDeclFunctionArgNoWarningTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        runAlgorithm(new OrganizeIncludesAlgorithm());
        assertStatusOk();
    }
}
