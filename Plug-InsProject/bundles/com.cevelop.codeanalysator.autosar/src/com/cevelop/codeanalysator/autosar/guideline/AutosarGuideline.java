package com.cevelop.codeanalysator.autosar.guideline;

import com.cevelop.codeanalysator.autosar.quickfix.DoNotImplicitlyCaptureVariablesQuickFix;
import com.cevelop.codeanalysator.autosar.quickfix.DoNotIntroduceVirtualFunctionInFinalClassQuickFix;
import com.cevelop.codeanalysator.autosar.quickfix.DoNotOmitVirtSpecifierAddFinalQuickFix;
import com.cevelop.codeanalysator.autosar.quickfix.DoNotOmitVirtSpecifierAddOverrideQuickFix;
import com.cevelop.codeanalysator.autosar.quickfix.DoNotUseRegisterQuickFix;
import com.cevelop.codeanalysator.autosar.quickfix.DoNotUseTypedefQuickFix;
import com.cevelop.codeanalysator.autosar.quickfix.MakeHexLiteralUppercaseQuickFix;
import com.cevelop.codeanalysator.autosar.quickfix.MandatoryLambdaParameterListQuickFix;
import com.cevelop.codeanalysator.autosar.quickfix.NoImplicitLambdaReturnTypeQuickFix;
import com.cevelop.codeanalysator.autosar.quickfix.ParenthesizeLogicalOperatorsQuickFix;
import com.cevelop.codeanalysator.autosar.quickfix.ReplaceAutoWithDeducedTypeQuickFix;
import com.cevelop.codeanalysator.autosar.quickfix.SwitchMustHaveAtLeastTwoCasesQuickFix;
import com.cevelop.codeanalysator.autosar.quickfix.UseBracedInitializationQuickFix;
import com.cevelop.codeanalysator.autosar.quickfix.VirtualFunctionShallHaveExactlyOneSpecifierQuickFix;
import com.cevelop.codeanalysator.autosar.util.AutosarIdHelper;
import com.cevelop.codeanalysator.core.CodeAnalysatorRuntime;
import com.cevelop.codeanalysator.core.guideline.IGuideline;
import com.cevelop.codeanalysator.core.guideline.ISuppressionStrategy;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.guideline.RuleRegistry;
import com.cevelop.codeanalysator.core.helper.CoreIdHelper;
import com.cevelop.codeanalysator.core.suppression.AttributeSuppressionStrategy;


public class AutosarGuideline implements IGuideline {

    public static Rule A02_13_05_HexValuesMustBeUppercase;
    public static Rule A04_07_01_AvoidLossyConversions;
    public static Rule A05_00_02_ConditionMustBeBool;
    public static Rule A05_00_03_NoMoreThanTwoLevelsOfPointerIndirection;
    public static Rule A05_01_01_DoNotUseLiteralValues;
    public static Rule A05_01_02_DoNotImplicitlyCaptureVariables;
    public static Rule A05_01_03_MandatoryLambdaParameterList;
    public static Rule A05_01_04_DoNotOutliveReferenceCapturedObjects;
    public static Rule A05_01_06_NoImplicitLambdaReturnType;
    public static Rule A05_01_08_DoNotNestLambdas;
    public static Rule A05_02_01_DoNotUseDynamicCast;
    public static Rule A05_02_06_ParenthesizeLogicalOperators;
    public static Rule A05_10_01_OnlyComparePointerToVirtualMemberFunctionWithNullPointer;
    public static Rule A05_16_01_DoNotUseTernaryConditionalOperatorAsSubExpression;
    public static Rule A06_04_01_SwitchMustHaveAtLeastTwoCases;
    public static Rule A07_01_04_DoNotUseRegister;
    public static Rule A07_01_05_UseAutoSparingly;
    public static Rule A07_01_06_DoNotUseTypedef;
    public static Rule A07_02_02_ExplicitlyDefineEnumUnderlyingBaseType;
    public static Rule A07_02_03_UseScopedEnumClasses;
    public static Rule A07_02_04_InitializeNoneFirstOrAll;
    public static Rule A07_04_01_DoNotUseAsm;
    public static Rule A07_05_01_DoNotReturnReferenceToParameterPassedByConstReference;
    public static Rule A08_05_00_AlwaysInitializeAnObject;
    public static Rule A08_05_02_UseBracedInitialization;
    public static Rule A08_05_03_DoNotInitializeAutoUsingInitializerList;
    public static Rule A09_05_01_DoNotUseUnion;
    public static Rule A10_01_01_NoDerivationFromMoreThanOneBaseClass;
    public static Rule A10_02_01_DoNotHideMemberFunctions;
    public static Rule A10_03_01_VirtualFunctionShallHaveExactlyOneSpecifier;
    public static Rule A10_03_02_DoNotOmitVirtSpecifier;
    public static Rule A10_03_03_DoNotIntroduceVirtualFunctionInFinalClass;
    public static Rule A10_03_05_NoVirtualAssignementOperators;
    public static Rule A12_00_01_MissingSpecialMemberFunctions;
    public static Rule A12_00_01_AvoidRedundantDefaultOperations;
    public static Rule A13_05_02_ExplicitConversionOperators;
    public static Rule A13_05_03_AvoidConversionOperators;
    public static Rule A15_03_05_CatchExceptionsByReference;

    private static final String autosarSuppressTag = "autosar";

    private AttributeSuppressionStrategy suppressionStrategy = new AttributeSuppressionStrategy(autosarSuppressTag, "autosar");

    public AutosarGuideline() {
        RuleRegistry registry = CodeAnalysatorRuntime.getDefault().getRuleRegistry();
        A02_13_05_HexValuesMustBeUppercase = registry.createAndRegisterRule("A2-13-5", this, AutosarIdHelper.HexValuesMustBeUppercaseProblemId);
        A04_07_01_AvoidLossyConversions = registry.createAndRegisterRule("A4-7-1", this, AutosarIdHelper.AvoidLossyConversionsProblemId,
                CoreIdHelper.AvoidLossyConversionsSharedProblemId);
        A05_00_02_ConditionMustBeBool = registry.createAndRegisterRule("A5-0-2", this, AutosarIdHelper.ConditionMustBeBoolProblemId);
        A05_00_03_NoMoreThanTwoLevelsOfPointerIndirection = registry.createAndRegisterRule("A5-0-3", this,
                AutosarIdHelper.NoMoreThanTwoLevelsOfIndirectionProblemId);
        A05_01_01_DoNotUseLiteralValues = registry.createAndRegisterRule("A5-1-1", this, AutosarIdHelper.DoNotUseLiteralValuesProblemId);
        A05_01_02_DoNotImplicitlyCaptureVariables = registry.createAndRegisterRule("A5-1-2", this,
                AutosarIdHelper.DoNotImplicitlyCaptureVariablesProblemId);
        A05_01_03_MandatoryLambdaParameterList = registry.createAndRegisterRule("A5-1-3", this,
                AutosarIdHelper.MandatoryLambdaParameterListProblemId);
        A05_01_04_DoNotOutliveReferenceCapturedObjects = registry.createAndRegisterRule("A5-1-4", this,
                AutosarIdHelper.DoNotOutliveReferenceCapturedObjectsProblemId);
        A05_01_06_NoImplicitLambdaReturnType = registry.createAndRegisterRule("A5-1-6", this,
                AutosarIdHelper.NoImplicitLambdaReturnTypeInfoProblemId);
        A05_01_08_DoNotNestLambdas = registry.createAndRegisterRule("A5-1-8", this, AutosarIdHelper.DoNotNestLambdasProblemId);
        A05_02_01_DoNotUseDynamicCast = registry.createAndRegisterRule("A5-2-1", this, AutosarIdHelper.DoNotUseDynamicCastProblemId);
        A05_02_06_ParenthesizeLogicalOperators = registry.createAndRegisterRule("A5-2-6", this,
                AutosarIdHelper.ParenthesizeLogicalOperatorsProblemId);
        A05_10_01_OnlyComparePointerToVirtualMemberFunctionWithNullPointer = registry.createAndRegisterRule("A5-10-1", this,
                AutosarIdHelper.OnlyComparePointerToVirtualMemberFunctionWithNullPointerProblemId);
        A05_16_01_DoNotUseTernaryConditionalOperatorAsSubExpression = registry.createAndRegisterRule("A5-16-1", this,
                AutosarIdHelper.DoNotUseTernaryConditionalOperatorAsSubExpressionProblemId);
        A06_04_01_SwitchMustHaveAtLeastTwoCases = registry.createAndRegisterRule("A6-4-1", this,
                AutosarIdHelper.SwitchMustHaveAtLeastTwoCasesProblemId);
        A07_01_04_DoNotUseRegister = registry.createAndRegisterRule("A7-1-4", this, AutosarIdHelper.DoNotUseRegisterProblemId);
        A07_01_05_UseAutoSparingly = registry.createAndRegisterRule("A7-1-5", this, AutosarIdHelper.UseAutoSparinglyProblemId);
        A07_01_06_DoNotUseTypedef = registry.createAndRegisterRule("A7-1-6", this, AutosarIdHelper.DoNotUseTypedefProblemId);
        A07_02_02_ExplicitlyDefineEnumUnderlyingBaseType = registry.createAndRegisterRule("A7-2-2", this,
                AutosarIdHelper.ExplicitlyDefineEnumUnderlyingBaseTypeProblemId);
        A07_02_03_UseScopedEnumClasses = registry.createAndRegisterRule("A7-2-3", this, AutosarIdHelper.UseScopedEnumClassesProblemId);
        A07_02_04_InitializeNoneFirstOrAll = registry.createAndRegisterRule("A7-2-4", this,
                AutosarIdHelper.InitializeNoneFirstOrAllEnumeratorsCheckerTestProblemId);
        A07_04_01_DoNotUseAsm = registry.createAndRegisterRule("7-4-1", this, AutosarIdHelper.DoNotUseAsmProblemId);
        A07_05_01_DoNotReturnReferenceToParameterPassedByConstReference = registry.createAndRegisterRule("A7-5-1", this,
                AutosarIdHelper.DoNotReturnReferenceToParameterPassedByConstReferenceProblemId);
        A08_05_00_AlwaysInitializeAnObject = registry.createAndRegisterRule("A8-5-0", this, AutosarIdHelper.AlwaysInitializeAnObjectProblemId,
                CoreIdHelper.AlwaysInitializeAnObjectSharedProblemId);
        A08_05_02_UseBracedInitialization = registry.createAndRegisterRule("A8-5-2", this, AutosarIdHelper.UseBracedInitializationProblemId);
        A08_05_03_DoNotInitializeAutoUsingInitializerList = registry.createAndRegisterRule("A8-5-3", this,
                AutosarIdHelper.DoNotInitializeAutoUsingInitializerListProblemId);
        A09_05_01_DoNotUseUnion = registry.createAndRegisterRule("A9-5-1", this, AutosarIdHelper.DoNotUseUnionProblemId);
        A10_01_01_NoDerivationFromMoreThanOneBaseClass = registry.createAndRegisterRule("A10-1-1", this,
                AutosarIdHelper.NoDerivationFromMoreThanOneBaseClassProblemId);
        A10_02_01_DoNotHideMemberFunctions = registry.createAndRegisterRule("A10-2-1", this, AutosarIdHelper.DoNotHideMemberFunctionsProblemId);
        A10_03_01_VirtualFunctionShallHaveExactlyOneSpecifier = registry.createAndRegisterRule("A10-3-1", this,
                AutosarIdHelper.VirtualFunctionShallHaveExactlyOneSpecifierProblemId);
        A10_03_02_DoNotOmitVirtSpecifier = registry.createAndRegisterRule("A10-3-2", this, AutosarIdHelper.DoNotOmitVirtSpecifierProblemId);
        A10_03_03_DoNotIntroduceVirtualFunctionInFinalClass = registry.createAndRegisterRule("A10-3-3", this,
                AutosarIdHelper.DoNotIntroduceVirtualFunctionInFinalClassProblemId);
        A10_03_05_NoVirtualAssignementOperators = registry.createAndRegisterRule("A10-3-5", this,
                AutosarIdHelper.NoVirtualAssignmentOperatorsProblemId);
        A12_00_01_MissingSpecialMemberFunctions = registry.createAndRegisterRule("A12-0-1", this,
                AutosarIdHelper.MissingSpecialMemberFunctionsProblemId, CoreIdHelper.MissingSpecialMemberFunctionsSharedProblemId);
        A12_00_01_AvoidRedundantDefaultOperations = registry.createAndRegisterRule("A12-0-1", this, AutosarIdHelper.RedundantOperationsProblemId,
                CoreIdHelper.RedundantOperationsSharedProblemId);
        A13_05_02_ExplicitConversionOperators = registry.createAndRegisterRule("A13-5-2", this, AutosarIdHelper.ExplicitConversionOperatorsProblemId);
        A13_05_03_AvoidConversionOperators = registry.createAndRegisterRule("A13-5-3", this, AutosarIdHelper.AvoidConversionOperatorsProblemId,
                CoreIdHelper.AvoidConversionOperatorsSharedProblemId);
        A15_03_05_CatchExceptionsByReference = registry.createAndRegisterRule("A15-3-5", this, AutosarIdHelper.CatchExceptionsByReferenceProblemId);

        A02_13_05_HexValuesMustBeUppercase.addQuickFixes(new MakeHexLiteralUppercaseQuickFix("A2-13-5: Make Hexadecimal constant uppercase"));
        A05_01_02_DoNotImplicitlyCaptureVariables.addQuickFixes(new DoNotImplicitlyCaptureVariablesQuickFix(
                "A5-1-2: Make implicit captures explicit"));
        A05_01_03_MandatoryLambdaParameterList.addQuickFixes(new MandatoryLambdaParameterListQuickFix("A5-1-3: Specify lambda parameter list"));
        A05_01_06_NoImplicitLambdaReturnType.addQuickFixes(new NoImplicitLambdaReturnTypeQuickFix("A5-1-6: Make lambda return type explicit"));
        A05_02_06_ParenthesizeLogicalOperators.addQuickFixes(new ParenthesizeLogicalOperatorsQuickFix("A5-2-6: Parenthesize binary sub expressinos"));
        A06_04_01_SwitchMustHaveAtLeastTwoCases.addQuickFixes(new SwitchMustHaveAtLeastTwoCasesQuickFix("A6-4-1: Replace switch with if-else"));
        A07_01_04_DoNotUseRegister.addQuickFixes(new DoNotUseRegisterQuickFix("A7-1-4: Remove register keyword"));
        A07_01_05_UseAutoSparingly.addQuickFixes(new ReplaceAutoWithDeducedTypeQuickFix("A7-1-5: Replace auto with deduced type"));
        A07_01_06_DoNotUseTypedef.addQuickFixes(new DoNotUseTypedefQuickFix("A7-1-6: Replace typedef with using"));
        A08_05_02_UseBracedInitialization.addQuickFixes(new UseBracedInitializationQuickFix("A8-5-2: Use braced-initialization {}"));
        A08_05_03_DoNotInitializeAutoUsingInitializerList.addQuickFixes(new ReplaceAutoWithDeducedTypeQuickFix(
                "A8-5-3: Replace auto with deduced type"));
        A10_03_01_VirtualFunctionShallHaveExactlyOneSpecifier.addQuickFixes(new VirtualFunctionShallHaveExactlyOneSpecifierQuickFix(
                "A10-3-1: Fix virtual specifiers"));
        A10_03_02_DoNotOmitVirtSpecifier.addQuickFixes(new DoNotOmitVirtSpecifierAddOverrideQuickFix("A10-3-2: add override specifier"),
                new DoNotOmitVirtSpecifierAddFinalQuickFix("A10-3-2: add final specifier"));
        A10_03_03_DoNotIntroduceVirtualFunctionInFinalClass.addQuickFixes(new DoNotIntroduceVirtualFunctionInFinalClassQuickFix(
                "A10-3-3: Make Final"));
    }

    @Override
    public String getName() {
        return "AUTOSAR Guideline";
    }

    @Override
    public String getId() {
        return AutosarIdHelper.GuidelineId;
    }

    @Override
    public ISuppressionStrategy getSuppressionStrategy() {
        return suppressionStrategy;
    }

}
