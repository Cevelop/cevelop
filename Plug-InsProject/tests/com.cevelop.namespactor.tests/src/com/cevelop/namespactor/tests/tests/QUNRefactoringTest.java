/******************************************************************************
 * Copyright (c) 2012 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 ******************************************************************************/
package com.cevelop.namespactor.tests.tests;

import org.eclipse.cdt.core.model.CModelException;

import com.cevelop.namespactor.refactoring.qun.QUNRefactoring;
import com.cevelop.namespactor.tests.NamespactorTest;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;


public class QUNRefactoringTest extends NamespactorTest {

    @Override
    protected CRefactoring getRefactoring() throws CModelException {
        return new QUNRefactoring(getPrimaryCElementFromCurrentProject().get(), getSelectionOfPrimaryTestFile());
    }

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("include");
        super.initAdditionalIncludes();
    }
}
