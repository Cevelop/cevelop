package com.cevelop.codeanalysator.misra.guideline;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.core.CodeAnalysatorRuntime;
import com.cevelop.codeanalysator.core.guideline.IGuideline;
import com.cevelop.codeanalysator.core.guideline.ISuppressionStrategy;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.guideline.RuleRegistry;
import com.cevelop.codeanalysator.core.helper.CoreIdHelper;
import com.cevelop.codeanalysator.core.quickfix.shared.DeclareLoopVariableInTheInitializerQuickFix;
import com.cevelop.codeanalysator.core.suppression.AttributeSuppressionStrategy;
import com.cevelop.codeanalysator.misra.util.MisraIdHelper;


public class MisraGuideline implements IGuideline {

    public static Rule M03_04_01_DeclareLoopVariableInTheIntializer;
    public static Rule M04_05_01_BoolExpressionOperandsProblem;
    public static Rule M04_05_01_BoolExpressionOperandsInfo;
    public static Rule M05_00_06_AvoidLossyConversion;

    private static final String misraSuppressTag = "misra";

    private AttributeSuppressionStrategy suppressionStrategy = new AttributeSuppressionStrategy(misraSuppressTag, "misra");

    public MisraGuideline() {
        RuleRegistry registry = CodeAnalysatorRuntime.getDefault().getRuleRegistry();
        M03_04_01_DeclareLoopVariableInTheIntializer = registry.createAndRegisterRule("M3-4-1", this,
                MisraIdHelper.DeclareLoopVariableInTheIntializerProblemId, CoreIdHelper.DeclareLoopVariableInTheIntializerSharedProblemId);
        M04_05_01_BoolExpressionOperandsProblem = registry.createAndRegisterRule("M4-5-1", this, MisraIdHelper.BoolExpressionOperandsProblemId);
        M04_05_01_BoolExpressionOperandsInfo = registry.createAndRegisterRule("M4-5-1", this, MisraIdHelper.BoolExpressionOperandsInfoProblemId);
        M05_00_06_AvoidLossyConversion = registry.createAndRegisterRule("M5-0-6", this, MisraIdHelper.AvoidLossyConversionsProblemId,
                CoreIdHelper.AvoidLossyConversionsSharedProblemId);

        M03_04_01_DeclareLoopVariableInTheIntializer.addQuickFixes(new IMarkerResolution[] { new DeclareLoopVariableInTheInitializerQuickFix(
                "M3-4-1: Declare variable in the loop initializer") });
    }

    @Override
    public String getName() {
        return "MISRA Guideline";
    }

    @Override
    public String getId() {
        return MisraIdHelper.GuidelineId;
    }

    @Override
    public ISuppressionStrategy getSuppressionStrategy() {
        return suppressionStrategy;
    }
}
