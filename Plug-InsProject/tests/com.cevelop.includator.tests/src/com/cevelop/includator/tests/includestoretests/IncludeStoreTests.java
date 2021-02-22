/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.includestoretests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
			IncludePathStore1DuplicateTest.class,
			RecursiveIndexIncludeStore2InactiveIncludeTest.class,
			IndexIncludeStore3InactiveIncludeTest.class
			//@formatter:on
})
public class IncludeStoreTests {}
