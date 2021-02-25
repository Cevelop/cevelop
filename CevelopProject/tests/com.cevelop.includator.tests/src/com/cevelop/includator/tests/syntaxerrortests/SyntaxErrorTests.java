/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.syntaxerrortests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
			SyntaxError1MissingOpeningBracket.class,
			SyntaxError2MissingSemicolonAfterIName.class,
//			SyntaxError3UnresolvableBaseClass.class, //Note: CDT since version 9.3.0 resolves A to NS::A
			SyntaxError4UnresolvableMemberInUsing.class,
			SyntaxError5ProblemBindingInCSimpleDeclaration.class
			//@formatter:on
})
public class SyntaxErrorTests {}
