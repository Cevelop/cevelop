/******************************************************************************
 * Copyright (c) 2014, 2015 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Thomas Corbat - initial API and implementation
 ******************************************************************************/
package com.cevelop.elevator.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.elevator.tests.checker.DefaultConstructorNegativeMatchesTest;
import com.cevelop.elevator.tests.checker.DefaultConstructorPositiveMatchesTest;
import com.cevelop.elevator.tests.checker.InitializationCheckerNegativeMatchesTest;
import com.cevelop.elevator.tests.checker.InitializationCheckerPositiveMatchesTest;
import com.cevelop.elevator.tests.checker.NullMacroCheckerTest;
import com.cevelop.elevator.tests.quickfix.InitializationQuickFixTest;
import com.cevelop.elevator.tests.quickfix.ReplaceNullMacroQuickFixTest;
import com.cevelop.elevator.tests.refactoring.ElevateProjectRefactoringTest;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
	InitializationCheckerNegativeMatchesTest.class,
	InitializationCheckerPositiveMatchesTest.class,
	InitializationQuickFixTest.class,
	ElevateProjectRefactoringTest.class,
	DefaultConstructorNegativeMatchesTest.class,
	DefaultConstructorPositiveMatchesTest.class,
	NullMacroCheckerTest.class,
	ReplaceNullMacroQuickFixTest.class
//@formatter:on
})
public class PluginUITestSuiteAll {}
