/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.returnstatustests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
			ReturnStatus1OkStatus.class,
			ReturnStatus2ExceptionStatus.class,
			ReturnStatus3ProblemBinding.class,
			ReturnStatus4DuplicateDeclFunctionArgNoWarningTest.class,
			ReturnStatus5IncludingUnknownFileExtension.class,
			//@formatter:on
})
public class ReturnStatusTests {}
