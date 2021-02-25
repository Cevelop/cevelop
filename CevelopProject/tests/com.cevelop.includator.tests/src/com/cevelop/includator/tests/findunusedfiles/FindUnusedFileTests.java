/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.findunusedfiles;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			FindUnusedFile1Simple.class,
			FindUnusedFile2SeveralSourceFiles.class,
			FindUnusedFile3SeveralMatches.class,
			FindUnusedFile4GroupMatches.class,
			FindUnusedFile5UnresolvableInclue.class
			//@formatter:on
})
public class FindUnusedFileTests {}
