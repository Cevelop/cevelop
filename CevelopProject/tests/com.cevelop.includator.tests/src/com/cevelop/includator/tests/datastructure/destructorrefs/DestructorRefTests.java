/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.destructorrefs;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			DestructorRefTest1tu.class,
			DestructorRefTest2function.class,
			DestructorRefTest3MemberVar.class,
			DestructorRefTest4ParameterDecl.class,
			DestructorRefTest5tuNoImpl.class,
			DestructorRefTest6FunctionNoImpl.class,
			DestructorRefTest7MemberVarNoImpl.class,
			DestructorRefTest8ParameterDeclNoImpl.class,
			DestructorRefTest9pointer.class,
			DestructorRefTest10ExplDestrExplSubDestr.class,
			DestructorRefTest11ExplDestrImplicitSubDestr.class,
			DestructorRefTest12BaseClassDestructor.class,
			DestructorRefTest13BaseClassDestructor2.class,
			DestructorRefTest14BaseClassDestructor3.class
//			DestructorRefTest15ExplicitDestrCall.class
			//@formatter:on
})
public class DestructorRefTests {}
