package com.cevelop.codeanalysator.autosar.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.codeanalysator.autosar.tests.checker.AlwaysInitializeAnObjectCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.AvoidConversionOperatorsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.AvoidLossyFloatingPointConversionsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.AvoidLossyFloatingPointFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.AvoidLossyFloatingPointToIntegerConversionsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.AvoidLossyFloatingPointToIntegerFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.AvoidLossyIntToCharBigConversionsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.AvoidLossyIntToCharBigFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.AvoidLossyIntToCharSmallConversionsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.AvoidLossyIntToCharSmallFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.AvoidLossyIntegerConversionsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.AvoidLossyIntegerFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.AvoidLossySignedToUnsignedConversionsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.CatchExceptionsByReferenceCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.ConditionMustBeBoolCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotHideMemberFunctionsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotImplicitlyCaptureVariablesCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotInitializeAutoUsingInitializerListCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotIntroduceVirtualFunctionInFinalClassCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotNestLambdasCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotOmitVirtSpecifierCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotOutliveReferenceCapturedObjectsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotReturnReferenceToParameterPassedByConstReferenceCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotUseAsmCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotUseDynamicCastCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotUseLiteralValuesCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotUseRegisterCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotUseTernaryConditionalOperatorAsSubExpressionCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotUseTypedefCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.DoNotUseUnionCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.ExplicitConversionOperatorsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.ExplicitlyDefineEnumUnderlyingBaseTypeCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.HexValuesMustBeUppercaseCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.InitializeNoneFirstOrAllEnumeratorsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.MandatoryLambdaParameterListCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.MissingSpecialMemberFunctionsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.NoDerivationFromMoreThanOneBaseClassCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.NoImplicitLambdaReturnTypeCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.NoMoreThanTwoLevelsOfPointerIndirectionCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.NoVirtualAssignmentOperatorsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.OnlyComparePointerToVirtualMemberFunctionWithNullPointerCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.ParenthesizeLogicalOperatorsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.RedundantOperationsCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.SwitchMustHaveAtLeastTwoCasesCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.UseAutoSparinglyCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.UseBracedInitializationCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.UseScopedEnumClassesCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.checker.VirtualFunctionShallHaveExactlyOneSpecifierCheckerTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.AutosarSuppressGuidelineQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.DoNotImplicitlyCaptureVariablesQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.DoNotInitializeAutoUsingInitializerListQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.DoNotIntroduceVirtualFunctionInFinalClassQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.DoNotOmitVirtSpecifierAddFinalQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.DoNotOmitVirtSpecifierAddOverrideQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.DoNotUseRegisterQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.DoNotUseTypedefQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.MakeHexLiteralUppercaseQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.MandatoryLambdaParameterListQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.NoImplicitLambdaReturnTypeQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.ParenthesizeLogicalOperatorsQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.SuppressAllGuidelinesQuickfixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.SwitchMustHaveAtLeastTwoCasesQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.UseAutoSparinglyQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.UseBracedInitializationQuickFixTest;
import com.cevelop.codeanalysator.autosar.tests.quickfix.VirtualFunctionShallHaveExactlyOneSpecifierQuickFixTest;


@RunWith(Suite.class)
@SuiteClasses({
   // @formatter:off
   // Checkers
   AlwaysInitializeAnObjectCheckerTest.class,
   AvoidConversionOperatorsCheckerTest.class,
   AvoidLossyFloatingPointConversionsCheckerTest.class,
   AvoidLossyFloatingPointFunctionArgumentConversionsCheckerTest.class,
   AvoidLossyFloatingPointToIntegerConversionsCheckerTest.class,
   AvoidLossyFloatingPointToIntegerFunctionArgumentConversionsCheckerTest.class,
   AvoidLossyIntToCharBigConversionsCheckerTest.class,
   AvoidLossyIntToCharBigFunctionArgumentConversionsCheckerTest.class,
   AvoidLossyIntToCharSmallConversionsCheckerTest.class,
   AvoidLossyIntToCharSmallFunctionArgumentConversionsCheckerTest.class,
   AvoidLossyIntegerConversionsCheckerTest.class,
   AvoidLossyIntegerFunctionArgumentConversionsCheckerTest.class,
   AvoidLossySignedToUnsignedConversionsCheckerTest.class,
   CatchExceptionsByReferenceCheckerTest.class,
   ConditionMustBeBoolCheckerTest.class,
   DoNotHideMemberFunctionsCheckerTest.class,
   DoNotImplicitlyCaptureVariablesCheckerTest.class,
   DoNotInitializeAutoUsingInitializerListCheckerTest.class,
   DoNotIntroduceVirtualFunctionInFinalClassCheckerTest.class,
   DoNotNestLambdasCheckerTest.class,
   DoNotOmitVirtSpecifierCheckerTest.class,
   DoNotOutliveReferenceCapturedObjectsCheckerTest.class,
   DoNotReturnReferenceToParameterPassedByConstReferenceCheckerTest.class,
   DoNotUseAsmCheckerTest.class,
   DoNotUseDynamicCastCheckerTest.class,
   DoNotUseLiteralValuesCheckerTest.class,
   DoNotUseRegisterCheckerTest.class,
   DoNotUseTernaryConditionalOperatorAsSubExpressionCheckerTest.class,
   DoNotUseTypedefCheckerTest.class,
   DoNotUseUnionCheckerTest.class,
   ExplicitConversionOperatorsCheckerTest.class,
   ExplicitlyDefineEnumUnderlyingBaseTypeCheckerTest.class,
   HexValuesMustBeUppercaseCheckerTest.class,
   InitializeNoneFirstOrAllEnumeratorsCheckerTest.class,
   MandatoryLambdaParameterListCheckerTest.class,
   MissingSpecialMemberFunctionsCheckerTest.class,
   NoDerivationFromMoreThanOneBaseClassCheckerTest.class,
   NoImplicitLambdaReturnTypeCheckerTest.class,
   NoMoreThanTwoLevelsOfPointerIndirectionCheckerTest.class,
   NoVirtualAssignmentOperatorsCheckerTest.class,
   OnlyComparePointerToVirtualMemberFunctionWithNullPointerCheckerTest.class,
   ParenthesizeLogicalOperatorsCheckerTest.class,
   RedundantOperationsCheckerTest.class,
   SwitchMustHaveAtLeastTwoCasesCheckerTest.class,
   UseAutoSparinglyCheckerTest.class,
   UseBracedInitializationCheckerTest.class,
   UseScopedEnumClassesCheckerTest.class,
   VirtualFunctionShallHaveExactlyOneSpecifierCheckerTest.class,
   // Quick fixes
   SuppressAllGuidelinesQuickfixTest.class,
   AutosarSuppressGuidelineQuickFixTest.class,
   DoNotImplicitlyCaptureVariablesQuickFixTest.class,
   DoNotInitializeAutoUsingInitializerListQuickFixTest.class,
   DoNotIntroduceVirtualFunctionInFinalClassQuickFixTest.class,
   DoNotOmitVirtSpecifierAddFinalQuickFixTest.class,
   DoNotOmitVirtSpecifierAddOverrideQuickFixTest.class,
   DoNotUseRegisterQuickFixTest.class,
   DoNotUseTypedefQuickFixTest.class,
   MakeHexLiteralUppercaseQuickFixTest.class,
   MandatoryLambdaParameterListQuickFixTest.class,
   NoImplicitLambdaReturnTypeQuickFixTest.class,
   ParenthesizeLogicalOperatorsQuickFixTest.class,
   SwitchMustHaveAtLeastTwoCasesQuickFixTest.class,
   UseAutoSparinglyQuickFixTest.class,
   UseBracedInitializationQuickFixTest.class,
   VirtualFunctionShallHaveExactlyOneSpecifierQuickFixTest.class
	// @formatter:on
})
public class PluginUITestSuiteAll {}
