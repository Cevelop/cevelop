/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.findunusedtests;

import org.junit.Test;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.actions.DirectlyOrganizeIncludesAction;


public class RemoveUnused22OffsetTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        runAction(new DirectlyOrganizeIncludesAction(), AlgorithmScope.FILE_SCOPE);
        assertAllSourceFilesEqual(COMPARE_AST_AND_COMMENTS_AND_INCLUDES);
    }
}
