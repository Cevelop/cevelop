/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpertests;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.helpers.tuprovider.TranslationUnitProvider;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest9LocationTuProviderTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        IncludatorFile file = getActiveIncludatorFile();
        TranslationUnitProvider tuProvider = new TranslationUnitProvider(file.getIFile());
        IASTTranslationUnit ast = tuProvider.getASTTranslationUnit(getActiveProject().getIndex());
        Assert.assertTrue(ast instanceof ICPPASTTranslationUnit);
        Assert.assertEquals(file.getFilePath(), ast.getFilePath());
    }
}
