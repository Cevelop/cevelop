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
import org.eclipse.core.runtime.IPath;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.startingpoints.ActiveEditorStartingPoint;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest11ExternalEditorFileTest extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("externalFrameworkTest");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        IPath path = externalTestResourcesHolder.makeProjectAbsolutePath("externalFrameworkTest/QWER.h");
        openExternalFileInEditor(path);
        // ITranslationUnit unit = CoreModel.getDefault().createTranslationUnitFrom(cproject, new Path(path));
        // ExternalEditorInput input = new ExternalEditorInput(unit);
        // IncludatorPlugin.getActiveWorkbenchWindow().getActivePage().openEditor(input, EditorUtility.getEditorID(input, null), true);
        IncludatorFile file = new ActiveEditorStartingPoint(IncludatorPlugin.getActiveWorkbenchWindow()).getFile();
        IASTTranslationUnit ast = file.getTranslationUnit();
        Assert.assertTrue(ast instanceof ICPPASTTranslationUnit);
        Assert.assertEquals(path.toOSString(), ast.getFilePath());
    }
}
