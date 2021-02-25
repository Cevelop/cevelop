/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.testframeworktest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
			ExternalIncludeDependencyTestFrameworkTest.class,
			RefactoringTestFrameworkTest.class,
			ReferencedProjectTest.class,
			CheckNoUnresolvedInclusionsTest.class
			//@formatter:on
})
public class TestFrameworkTests {}
