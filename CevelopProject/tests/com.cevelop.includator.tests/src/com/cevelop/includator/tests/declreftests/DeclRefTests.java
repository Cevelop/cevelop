/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.declreftests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			DeclRefTest1GroupedDeclarations.class,
			DeclRefTest2ForwardDeclEnough1.class,
			DeclRefTest3ForwardDeclEnough2.class,
			DeclRefTest4ForwardDeclEnough3ManySimilarDecls.class,
			DeclRefTest5ForwardDeclEnough4ManyDeclsOneStatement.class,
			DeclRefTest6ForwardDeclEnough5FunctionArgs.class,
			DeclRefTest7ClassRefInMethodImplTest.class,
			DeclRefTest8StructRefInMethodImplTest.class,
			DeclRefTest9NamespaceAroundMethodDef.class,
			DeclRefTest10ForwardDeclEnough6ArgOfFunctionDefinition.class,
			DeclRefTest11ForwardDeclEnough7ArgOfFunctionDeclaration.class,
			DeclRefTest12ForwardDeclEnough8Expressions.class,
			DeclRefTest13QualifiedStaticTemplateFunctionRef.class,
			DeclRefTest14CLinkageDesignatedInitializerTest.class,
			DeclRefTest15ImplicitFunction.class,
			DeclRefTest16ForwardDeclEnough8Expression2.class,
			DeclRefTest17NonIncludedMacro.class,
			DeclRefTest18DeclRefToStringOfExpr.class,
			DeclRefTest19ParamFunctionParamNameNullBindingNoWarning.class,
			DeclRefTest20ForwardDeclEnough9FunctionDefReturnType.class,
			//@formatter:on
})
public class DeclRefTests {}
