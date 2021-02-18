/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureIncludeStatementTest1 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<IASTPreprocessorIncludeStatement> includes = getActiveIncludatorFile().getIncludes();

        Assert.assertEquals((Object) 1, (Object) includes.size());
        IASTPreprocessorIncludeStatement include1 = includes.get(0);
        Assert.assertEquals("#include <iostream>", include1.toString());
    }
}
