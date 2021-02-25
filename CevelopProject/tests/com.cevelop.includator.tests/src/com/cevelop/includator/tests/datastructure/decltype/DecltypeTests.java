/*******************************************************************************
 * Copyright (c) 2014 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Thomas Corbat (IFS) - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.decltype;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			Decltype01LocalDecltype.class,
			Decltype02DecltypeAsQualifier.class,
			Decltype03DecltypeAsQualifierWithInclude.class,
			Decltype04DecltypeExternalExpression.class
			//@formatter:on
})
public class DecltypeTests {}
