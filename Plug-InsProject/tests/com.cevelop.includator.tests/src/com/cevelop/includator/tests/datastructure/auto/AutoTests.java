/*******************************************************************************
 * Copyright (c) 2014 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Thomas Corbat (IFS) - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.auto;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			Auto01LocalAuto.class,
			Auto02AutoForExternalExpression.class,
			Auto03AutoReturnType.class
			//@formatter:on
})
public class AutoTests {}
