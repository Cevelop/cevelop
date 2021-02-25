/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.includetofwd;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			IncludeToFwd1Replacable.class,
			IncludeToFwd2NotReplacable.class,
			IncludeToFwd3SeveralPathsBothPossible.class,
			IncludeToFwd4SeveralPathsOnePossible.class,
			IncludeToFwd5SeveralRefOneGood.class,
			IncludeToFwd6FunctionRefParam.class,
			IncludeToFwd7SeveralFwdInSameHeader.class,
			IncludeToFwd8Function.class,
			IncludeToFwd9FunctionRefParamUnreplacable.class,
			IncludeToFwd10Struct.class,
			IncludeToFwd11IncludeToInexistingHeader.class,
			IncludeToFwd12FunctionCallInTemplateFunction.class,
			IncludeToFwd13QuickFixClass.class,
			IncludeToFwd14QuickFixClassWithTemplate.class,
			IncludeToFwd15QuickFixFunctionWithParams.class,
			IncludeToFwd16QuickFixFunctionWithParamsAndTemplate.class,
			IncludeToFwd17QuickFixTypeDef.class,
			IncludeToFwd18TemplateWithTypedef.class,
			IncludeToFwd19QuickFixWithUserTyping.class,
			IncludeToFwd20QuickFixWithUserTyping2.class,
			IncludeToFwd21QuickFixAndUndo.class,
			IncludeToFwd22SeveralQuickFixes.class,
			IncludeToFwd23SeveralQuickFixesAndUndos.class,
			IncludeToFwd24SeveralQuickFixesAndUndosWithUserTyping.class,
			IncludeToFwd31SeveralInProject.class,
			IncludeToFwd32Variable.class,
			IncludeToFwd33ExternalFunctionRef.class
			//@formatter:on
})
public class IncludeToFwdTests {}
