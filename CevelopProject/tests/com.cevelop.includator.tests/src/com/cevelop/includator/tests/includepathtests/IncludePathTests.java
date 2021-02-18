/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.includepathtests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			IncludePathTest1SimpleTest.class,
			IncludePathTest2Simple3PathElements.class,
			IncludePathTest3DoublePath1.class,
			IncludePathTest4DoublePath2.class,
			IncludePathTest5ManyPaths.class,
			IncludePathTest6CircularIncludes.class,
			IncludePathTest7CircularIncludes2.class,
			IncludePathTest8InexistingInclude.class
			//@formatter:on
})
public class IncludePathTests {}
