/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.tdd.ui.tests.quickfixes;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ AddArgumentTest.class, //
                ChangeVisibilityTest.class, //
                CreateConstructorTest.class, //
                CreateFreeFunctionTest.class, //
                CreateFreeOperatorTest.class, //
                CreateMemberFunctionTest.class, //
                CreateLocalVariableTest.class, //
                CreateMemberOperatorTest.class, //
                CreateMemberVariableTest.class, //
                CreateNamespaceTest.class, //
                CreateStaticMemberFunctionTest.class, //
                CreateTypeTest.class,//
})
public class QuickFixTestSuiteAll {}
