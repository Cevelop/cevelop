/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.indexadaptiontests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			IndexAdaptionTest1UnincludedHeader.class,
			IndexAdaptionTest2ExternalHeaderSimple.class,
			IndexAdaptionTest3IncludePathSubdirUnavailable.class,
			IndexAdaptionTest4IncludePathSubdirAvailable.class,
			IndexAdaptionTest5IncludePathSubdirAvailable2.class,
			IndexAdaptionTest6IncludePathManySubdir.class,
			IndexAdaptionTest7WrongFileTypes.class,
			IndexAdaptionTest8WrongFileTypesNoExtension.class,
			//@formatter:on
})
public class IndexAdaptionTests {}
