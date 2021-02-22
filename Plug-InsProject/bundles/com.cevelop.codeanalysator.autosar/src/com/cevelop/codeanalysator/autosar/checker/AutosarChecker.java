package com.cevelop.codeanalysator.autosar.checker;

import java.util.ArrayList;
import java.util.Collection;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.visitor.CatchExceptionsByReferenceVisitor;
import com.cevelop.codeanalysator.autosar.visitor.ConditionMustBeBoolVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotHideMemberFunctionsVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotImpliciltyCaptureVariablesVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotInitializeAutoUsingInitializerListVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotIntroduceVirtualFunctionInFinalClassVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotNestLambdasVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotOmitVirtSpecifierVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotOutliveReferenceCapturedObjectsVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotReturnReferenceToParameterPassedByConstReferenceVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotUseAsmVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotUseDynamicCastVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotUseLiteralValuesVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotUseRegisterVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotUseTernaryConditionalOperatorAsSubExpressionVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotUseTypedefVisitor;
import com.cevelop.codeanalysator.autosar.visitor.DoNotUseUnionVisitor;
import com.cevelop.codeanalysator.autosar.visitor.ExplicitConversionOperatorsVisitor;
import com.cevelop.codeanalysator.autosar.visitor.ExplicitlyDefineEnumUnderlyingBaseTypeVisitor;
import com.cevelop.codeanalysator.autosar.visitor.HexValuesMustBeUppercaseVisitor;
import com.cevelop.codeanalysator.autosar.visitor.InitializeNoneFirstOrAllEnumeratorsVisitor;
import com.cevelop.codeanalysator.autosar.visitor.MandatoryLambdaParameterListVisitor;
import com.cevelop.codeanalysator.autosar.visitor.NoDerivationFromMoreThanOneBaseClassVisitor;
import com.cevelop.codeanalysator.autosar.visitor.NoImplicitLambdaReturnTypeVisitor;
import com.cevelop.codeanalysator.autosar.visitor.NoMoreThanTwoLevelsOfIndirection;
import com.cevelop.codeanalysator.autosar.visitor.NoVirtualAssignementOperatorsVisitor;
import com.cevelop.codeanalysator.autosar.visitor.OnlyComparePointerToVirtualMemberFunctionWithNullPointerVisitor;
import com.cevelop.codeanalysator.autosar.visitor.ParenthesizeLogicalOperatorsVisitor;
import com.cevelop.codeanalysator.autosar.visitor.SwitchMustHaveAtLeastTwoCasesVisitor;
import com.cevelop.codeanalysator.autosar.visitor.UseAutoSparinglyVisitor;
import com.cevelop.codeanalysator.autosar.visitor.UseBracedInitializationVisitor;
import com.cevelop.codeanalysator.autosar.visitor.UseScopedEnumClassesVisitor;
import com.cevelop.codeanalysator.autosar.visitor.VirtualFunctionShallHaveExactlyOneSpecifierVisitor;
import com.cevelop.codeanalysator.core.checker.CodeAnalysatorChecker;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;
import com.cevelop.codeanalysator.core.visitor.shared.AlwaysInitializeAnObjectVisitor;
import com.cevelop.codeanalysator.core.visitor.shared.AvoidConversionOperatorsVisitor;
import com.cevelop.codeanalysator.core.visitor.shared.AvoidLossyArithmeticConversionsVisitor;
import com.cevelop.codeanalysator.core.visitor.shared.MissingSpecialMemberFunctionsVisitor;
import com.cevelop.codeanalysator.core.visitor.shared.RedundantOperationsVisitor;


public class AutosarChecker extends CodeAnalysatorChecker {

    @Override
    protected Collection<CodeAnalysatorVisitor> createVisitors(RuleReporter ruleReporter) {
        Collection<CodeAnalysatorVisitor> visitors = new ArrayList<>();
        visitors.add(new AlwaysInitializeAnObjectVisitor(AutosarGuideline.A08_05_00_AlwaysInitializeAnObject, ruleReporter));
        visitors.add(new AvoidConversionOperatorsVisitor(AutosarGuideline.A13_05_03_AvoidConversionOperators, ruleReporter));
        visitors.add(new AvoidLossyArithmeticConversionsVisitor(AutosarGuideline.A04_07_01_AvoidLossyConversions, ruleReporter));
        visitors.add(new CatchExceptionsByReferenceVisitor(AutosarGuideline.A15_03_05_CatchExceptionsByReference, ruleReporter));
        visitors.add(new ConditionMustBeBoolVisitor(AutosarGuideline.A05_00_02_ConditionMustBeBool, ruleReporter));
        visitors.add(new DoNotHideMemberFunctionsVisitor(AutosarGuideline.A10_02_01_DoNotHideMemberFunctions, ruleReporter));
        visitors.add(new DoNotImpliciltyCaptureVariablesVisitor(AutosarGuideline.A05_01_02_DoNotImplicitlyCaptureVariables, ruleReporter));
        visitors.add(new DoNotInitializeAutoUsingInitializerListVisitor(AutosarGuideline.A08_05_03_DoNotInitializeAutoUsingInitializerList,
                ruleReporter));
        visitors.add(new DoNotIntroduceVirtualFunctionInFinalClassVisitor(AutosarGuideline.A10_03_03_DoNotIntroduceVirtualFunctionInFinalClass,
                ruleReporter));
        visitors.add(new DoNotNestLambdasVisitor(AutosarGuideline.A05_01_08_DoNotNestLambdas, ruleReporter));
        visitors.add(new DoNotOmitVirtSpecifierVisitor(AutosarGuideline.A10_03_02_DoNotOmitVirtSpecifier, ruleReporter));
        visitors.add(new DoNotOutliveReferenceCapturedObjectsVisitor(AutosarGuideline.A05_01_04_DoNotOutliveReferenceCapturedObjects, ruleReporter));
        visitors.add(new DoNotReturnReferenceToParameterPassedByConstReferenceVisitor(
                AutosarGuideline.A07_05_01_DoNotReturnReferenceToParameterPassedByConstReference, ruleReporter));
        visitors.add(new DoNotUseAsmVisitor(AutosarGuideline.A07_04_01_DoNotUseAsm, ruleReporter));
        visitors.add(new DoNotUseDynamicCastVisitor(AutosarGuideline.A05_02_01_DoNotUseDynamicCast, ruleReporter));
        visitors.add(new DoNotUseLiteralValuesVisitor(AutosarGuideline.A05_01_01_DoNotUseLiteralValues, ruleReporter));
        visitors.add(new DoNotUseRegisterVisitor(AutosarGuideline.A07_01_04_DoNotUseRegister, ruleReporter));
        visitors.add(new DoNotUseTernaryConditionalOperatorAsSubExpressionVisitor(
                AutosarGuideline.A05_16_01_DoNotUseTernaryConditionalOperatorAsSubExpression, ruleReporter));
        visitors.add(new DoNotUseTypedefVisitor(AutosarGuideline.A07_01_06_DoNotUseTypedef, ruleReporter));
        visitors.add(new DoNotUseUnionVisitor(AutosarGuideline.A09_05_01_DoNotUseUnion, ruleReporter));
        visitors.add(new ExplicitConversionOperatorsVisitor(AutosarGuideline.A13_05_02_ExplicitConversionOperators, ruleReporter));
        visitors.add(new ExplicitlyDefineEnumUnderlyingBaseTypeVisitor(AutosarGuideline.A07_02_02_ExplicitlyDefineEnumUnderlyingBaseType,
                ruleReporter));
        visitors.add(new HexValuesMustBeUppercaseVisitor(AutosarGuideline.A02_13_05_HexValuesMustBeUppercase, ruleReporter));
        visitors.add(new InitializeNoneFirstOrAllEnumeratorsVisitor(AutosarGuideline.A07_02_04_InitializeNoneFirstOrAll, ruleReporter));
        visitors.add(new MandatoryLambdaParameterListVisitor(AutosarGuideline.A05_01_03_MandatoryLambdaParameterList, ruleReporter));
        visitors.add(new MissingSpecialMemberFunctionsVisitor(AutosarGuideline.A12_00_01_MissingSpecialMemberFunctions, ruleReporter));
        visitors.add(new NoDerivationFromMoreThanOneBaseClassVisitor(AutosarGuideline.A10_01_01_NoDerivationFromMoreThanOneBaseClass, ruleReporter));
        visitors.add(new NoImplicitLambdaReturnTypeVisitor(AutosarGuideline.A05_01_06_NoImplicitLambdaReturnType, ruleReporter));
        visitors.add(new NoMoreThanTwoLevelsOfIndirection(AutosarGuideline.A05_00_03_NoMoreThanTwoLevelsOfPointerIndirection, ruleReporter));
        visitors.add(new NoVirtualAssignementOperatorsVisitor(AutosarGuideline.A10_03_05_NoVirtualAssignementOperators, ruleReporter));
        visitors.add(new OnlyComparePointerToVirtualMemberFunctionWithNullPointerVisitor(
                AutosarGuideline.A05_10_01_OnlyComparePointerToVirtualMemberFunctionWithNullPointer, ruleReporter));
        visitors.add(new ParenthesizeLogicalOperatorsVisitor(AutosarGuideline.A05_02_06_ParenthesizeLogicalOperators, ruleReporter));
        visitors.add(new RedundantOperationsVisitor(AutosarGuideline.A12_00_01_AvoidRedundantDefaultOperations, ruleReporter));
        visitors.add(new SwitchMustHaveAtLeastTwoCasesVisitor(AutosarGuideline.A06_04_01_SwitchMustHaveAtLeastTwoCases, ruleReporter));
        visitors.add(new UseAutoSparinglyVisitor(AutosarGuideline.A07_01_05_UseAutoSparingly, ruleReporter));
        visitors.add(new UseBracedInitializationVisitor(AutosarGuideline.A08_05_02_UseBracedInitialization, ruleReporter));
        visitors.add(new UseScopedEnumClassesVisitor(AutosarGuideline.A07_02_03_UseScopedEnumClasses, ruleReporter));
        visitors.add(new VirtualFunctionShallHaveExactlyOneSpecifierVisitor(AutosarGuideline.A10_03_01_VirtualFunctionShallHaveExactlyOneSpecifier,
                ruleReporter));
        return visitors;
    }
}
