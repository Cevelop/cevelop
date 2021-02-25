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
package com.cevelop.namespactor.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.namespactor.tests.checker.UsingCheckerTest;
import com.cevelop.namespactor.tests.tests.EUDecRefactoringTest;
import com.cevelop.namespactor.tests.tests.EUDirRefactoringTest;
import com.cevelop.namespactor.tests.tests.ITDARefactoringTest;
import com.cevelop.namespactor.tests.tests.IUDecRefactoringTest;
import com.cevelop.namespactor.tests.tests.IUDirRefactoringTest;
import com.cevelop.namespactor.tests.tests.QUNRefactoringTest;
import com.cevelop.namespactor.tests.tests.TD2ARefactoringTest;
import com.cevelop.namespactor.tests.tests.sandbox.LastExpressionStatementRefactoringTest;


@RunWith(Suite.class)
@SuiteClasses({
   // @formatter:off
   LastExpressionStatementRefactoringTest.class,
   ITDARefactoringTest.class,
   TD2ARefactoringTest.class,
   IUDirRefactoringTest.class,
   IUDecRefactoringTest.class,
   QUNRefactoringTest.class,
   EUDirRefactoringTest.class,
   EUDecRefactoringTest.class,
   UsingCheckerTest.class,
//   InlineTypeAliasRefactoringBugs.class,
   // @formatter:on
})
public class PluginUITestSuiteAll {}
