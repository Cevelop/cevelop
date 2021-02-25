/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.startingpointtests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
			StartingPointTest1FileScope.class,
			StartingPointTest2FolderScope.class,
			StartingPointTest3ProjectScope.class,
			StartingPointTest4ExternalFile.class,
			//@formatter:on
})
public class StartingPointTests {}
