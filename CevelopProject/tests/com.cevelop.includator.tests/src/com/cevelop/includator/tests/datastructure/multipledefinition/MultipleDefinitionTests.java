/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.multipledefinition;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			MultipleDefinitionTest1TypeDef.class,
			MultipleDefinitionTest2WeightSelection1.class,
			MultipleDefinitionTest3WeightSelection2.class,
			MultipleDefinitionTest4MacroDef.class,
			MultipleDefinitionTest5Type.class,
			MultipleDefinitionTest6Function.class,
			MultipleDefinitionTest7DefInCurrentSourceAndInHeader.class,
			MultipleDefinitionTest8DefInOtherSourceAndInHeader.class,
			MultipleDefinitionTest9DefExternalAndInternal.class,
			MultipleDefinitionTest10MultipleDeclWeightTest.class,
			MultipleDefinitionTest11TemplateFunctionWithSpecializations.class,
			MultipleDefinitionTest12PreferShortestRelativePath.class,
			//@formatter:on
})
public class MultipleDefinitionTests {}
