/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.constructorrefs;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			ConstructorRefTest1.class,
			ConstructorRefTest10CopyConstructor.class,
			ConstructorRefTest11ExplConstrExplSubConstr.class,
			ConstructorRefTest12ExplConstrExplSubConstr2.class,
			ConstructorRefTest13ExplConstrImplicitSubConstr.class,
			ConstructorRefTest14ImplConstrImplicitSubConstr.class,
			ConstructorRefTest15RefAndDefInSameFile.class,
			ConstructorRefTest16BaseClassConstructor.class,
			ConstructorRefTest17BaseClassConstructor2.class,
			ConstructorRefTest18BaseClassConstructor3.class,
			ConstructorRefTest19TemplateInstanceField.class,
			ConstructorRefTest2.class,
			ConstructorRefTest3.class,
			ConstructorRefTest4parameterDeclaration.class,
			ConstructorRefTest5.class,
			ConstructorRefTest6.class,
			ConstructorRefTest7.class,
			ConstructorRefTest8parameterDeclarationNoImpl.class,
			ConstructorRefTest9Pointer.class
			//@formatter:on
})
public class ConstructorRefTests {}
