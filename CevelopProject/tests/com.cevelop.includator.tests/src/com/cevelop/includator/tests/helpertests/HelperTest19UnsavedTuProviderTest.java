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
import org.eclipse.core.resources.IFile;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.helpers.tuprovider.TranslationUnitProvider;
import com.cevelop.includator.helpers.tuprovider.UnsavedEditorTranslationUnitProvider;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest19UnsavedTuProviderTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        IFile file = getActiveIncludatorFile().getIFile();
        openPrimaryTestFileInEditor();
        insertTextIntoActiveDocument(NL + "#include \"C.h\"", 14);

        TranslationUnitProvider savedTuProvider = new TranslationUnitProvider(file);
        IASTTranslationUnit savedAst = savedTuProvider.getASTTranslationUnit(getActiveProject().getIndex());

        Assert.assertEquals(testFiles.get(getNameOfPrimaryTestFile()).getSource(), savedAst.getRawSignature());

        TranslationUnitProvider unsavedTuProvider = new UnsavedEditorTranslationUnitProvider(file);
        IASTTranslationUnit unsavedAst = unsavedTuProvider.getASTTranslationUnit(getActiveProject().getIndex());

        Assert.assertEquals(getExpectedDocument(getExpectedIFile(getNameOfPrimaryTestFile())).get(), unsavedAst.getRawSignature());
    }
}
