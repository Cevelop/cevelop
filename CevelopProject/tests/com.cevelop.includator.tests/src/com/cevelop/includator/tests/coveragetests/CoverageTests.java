/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.coveragetests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			CoverageTest1Simple.class,
			CoverageTest2ManyFiles.class,
			CoverageTest3Action.class,
			CoverageTest4SystemIncludesTest.class,
			ProjectCoverageTest1WithMain.class,
			ProjectCoverageTest2WholeProject.class,
			ProjectCoverageTest3NameSpaces.class,
			ProjectCoverageTest4OnlyPartialClassImplUsed.class,
			ProjectCoverageTest5SkipImplWhenFwdEnough.class,
			ProjectCoverageTest6PointerToMember.class,
			ProjectCoverageTest7MethodParamNameBugFix.class,
			ProjectCoverageTest8GlobalVar1.class,
			ProjectCoverageTest9UsingNamespace.class,
			ProjectCoverageTest10UsingNamespace2.class,
			ProjectCoverageTest11DirectlyImplClass.class,
			ProjectCoverageTest12GlobalVar2.class,
			ProjectCoverageTest13GlobalVar3.class,
			ProjectCoverageTest14GlobalVar4.class,
			ProjectCoverageTest15DestructorTest1.class,
			ProjectCoverageTest16DestructorTest2.class,
			ProjectCoverageTest17FieldConstrDestr1.class,
			ProjectCoverageTest18FieldConstrDestr2.class,
			ProjectCoverageTest19FieldConstrDestr3.class,
			ProjectCoverageTest20FieldConstrDestr4.class,
			ProjectCoverageTest21FieldConstrDestr5.class,
			ProjectCoverageTest22FieldConstrDestr6.class,
			ProjectCoverageTest23ParentClassMethod.class,
			ProjectCoverageTest24OverrideMethod.class,
			ProjectCoverageTest25OverrideMethod2.class,
			ProjectCoverageTest26OverrideMethod3.class,
			ProjectCoverageTest27OverrideMethod4.class,
			ProjectCoverageTest28Typedef.class,
			ProjectCoverageTest29ImplicitlyUsed.class,
			ProjectCoverageTest30ImplicitlyUsed2.class,
			ProjectCoverageTest31ImplicitlyUsed3.class,
			ProjectCoverageTest32RecursionTest.class,
			ProjectCoverageTest33PointerField.class,
			ProjectCoverageTest34MinimalImplicitCtorRef.class,
			ProjectCoverageTest35ExplicitCopyCtor.class,
			ProjectCoverageTest36ImplicitCopyCtor.class,
			ProjectCoverageTest37ExplicitCopyCtorCallingCopyCtorOfField.class,
			ProjectCoverageTest38GlobalVarForClass.class,
			ProjectCoverageTest39GlobalPtrForClass.class,
			ProjectCoverageTest40TemplateClass.class,
			ProjectCoverageTest41TemplateFunction.class,
			ProjectCoverageTest42ClassDerivedFromTemplate.class,
			ProjectCoverageTest43Enum.class,
			ProjectCoverageTest44Enumerator.class,
			ProjectCoverageTest45NestedEnum.class,
			ProjectCoverageTest46NestedEnumNotUsed.class,
			ProjectCoverageTest47TemplFuncCallingImplDestr.class,
			ProjectCoverageTest48TemplPartialSpecialization.class,
			ProjectCoverageTest49AnonymousStruct.class,
			ProjectCoverageTest50OverloadedOperator.class,
			ProjectCoverageTest51WithIntMainVoid.class,
			ProjectCoverageTest52TemplateMemberFunction.class,
			ProjectCoverageTest53TemplateConstructor.class
			//@formatter:on
})
public class CoverageTests {}
