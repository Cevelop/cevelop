/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.organizeincludes;

import org.junit.Test;

import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class OrganizeIncludes22AddAfterCopyrightComment extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        applySuggestions(runAlgorithm(new OrganizeIncludesAlgorithm()));
        assertAllSourceFilesEqual(COMPARE_AST_AND_COMMENTS_AND_INCLUDES);
    }
}
