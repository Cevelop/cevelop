/******************************************************************************
 * Copyright (c) 2014 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Thomas Corbat - initial API and implementation
 ******************************************************************************/
package com.cevelop.macronator.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.macronator.tests.checker.FunctionLikeCheckerNegativeMatchesTest;
import com.cevelop.macronator.tests.checker.FunctionLikeCheckerPositiveMatchesTest;
import com.cevelop.macronator.tests.checker.ObjectLikeCheckerNegativeMatchesTest;
import com.cevelop.macronator.tests.checker.ObjectLikeCheckerPositiveMatchesTest;
import com.cevelop.macronator.tests.checker.UnusedMacroCheckerNegativeMatchesTest;
import com.cevelop.macronator.tests.checker.UnusedMacroCheckerPositiveMatchesTest;
import com.cevelop.macronator.tests.common.LexerAdapterTest;
import com.cevelop.macronator.tests.common.LocalExpansionTest;
import com.cevelop.macronator.tests.common.ParserAdapterTest;
import com.cevelop.macronator.tests.common.SuppressedMacrosTest;
import com.cevelop.macronator.tests.quickfix.FunctionLikeQuickfixTest;
import com.cevelop.macronator.tests.quickfix.ObjectLikeQuickfixTest;
import com.cevelop.macronator.tests.quickfix.UnusedMacroQuickfixTest;
import com.cevelop.macronator.tests.refactoring.ExpandMacroRefactoringTest;
import com.cevelop.macronator.tests.transform.AutoFunctionTransformationTest;
import com.cevelop.macronator.tests.transform.BuiltinMacroTest;
import com.cevelop.macronator.tests.transform.ConstexprTransformationTest;
import com.cevelop.macronator.tests.transform.DeclarationTransformationTest;
import com.cevelop.macronator.tests.transform.VoidFunctionTransformationTest;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
	FunctionLikeCheckerNegativeMatchesTest.class,
	FunctionLikeCheckerPositiveMatchesTest.class,
	ObjectLikeCheckerNegativeMatchesTest.class,
	ObjectLikeCheckerPositiveMatchesTest.class,
	UnusedMacroCheckerNegativeMatchesTest.class,
	UnusedMacroCheckerPositiveMatchesTest.class,
	LexerAdapterTest.class,
	LocalExpansionTest.class,
	ParserAdapterTest.class,
	SuppressedMacrosTest.class,
	FunctionLikeQuickfixTest.class,
	ObjectLikeQuickfixTest.class,
	UnusedMacroQuickfixTest.class,
	ExpandMacroRefactoringTest.class,
	AutoFunctionTransformationTest.class,
	BuiltinMacroTest.class,
	ConstexprTransformationTest.class,
	DeclarationTransformationTest.class,
	VoidFunctionTransformationTest.class
//@formatter:on
})
public class PluginUITestSuiteAll {}
