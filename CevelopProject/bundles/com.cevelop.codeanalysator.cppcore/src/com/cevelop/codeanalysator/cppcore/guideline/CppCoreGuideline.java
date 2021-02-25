package com.cevelop.codeanalysator.cppcore.guideline;

import com.cevelop.codeanalysator.core.CodeAnalysatorRuntime;
import com.cevelop.codeanalysator.core.guideline.IGuideline;
import com.cevelop.codeanalysator.core.guideline.ISuppressionStrategy;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.guideline.RuleRegistry;
import com.cevelop.codeanalysator.core.helper.CoreIdHelper;
import com.cevelop.codeanalysator.core.quickfix.shared.DeclareLoopVariableInTheInitializerQuickFix;
import com.cevelop.codeanalysator.core.suppression.AttributeSuppressionStrategy;
import com.cevelop.codeanalysator.cppcore.util.CppCoreIdHelper;


public class CppCoreGuideline implements IGuideline {

    public static Rule C_020_RedundantOperations;
    public static Rule C_021_MissingSpecialMemberFunctions;
    public static Rule C_031_NoDestructor;
    public static Rule C_031_DestructorHasNoBody;
    public static Rule C_031_DestructorWithMissingDeleteStatements;
    public static Rule C_164_AvoidConversionOperators;

    public static Rule ES_020_AlwaysInitializeAnObject;
    public static Rule ES_026_DontUseVariableForTwoUnrelatedPurposes;
    public static Rule ES_046_AvoidLossyConversions;
    public static Rule ES_074_DeclareLoopVariableInTheIntializer;

    private static final String cppCoreSuppressTag = "gsl";

    private AttributeSuppressionStrategy suppressionStrategy = new AttributeSuppressionStrategy(cppCoreSuppressTag, "c++ core");

    public CppCoreGuideline() {
        RuleRegistry registry = CodeAnalysatorRuntime.getDefault().getRuleRegistry();
        C_020_RedundantOperations = registry.createAndRegisterRule("C.20", this, CppCoreIdHelper.RedundantOperationsProblemId,
                CoreIdHelper.RedundantOperationsSharedProblemId);
        C_021_MissingSpecialMemberFunctions = registry.createAndRegisterRule("C.21", this, CppCoreIdHelper.MissingSpecialMemberFunctionsProblemId,
                CoreIdHelper.MissingSpecialMemberFunctionsSharedProblemId);
        C_031_NoDestructor = registry.createAndRegisterRule("C.31", this, CppCoreIdHelper.NoDestructorProblemId);
        C_031_DestructorHasNoBody = registry.createAndRegisterRule("C.31", this, CppCoreIdHelper.DestructorHasNoBodyProblemId);
        C_031_DestructorWithMissingDeleteStatements = registry.createAndRegisterRule("C.31", this,
                CppCoreIdHelper.DestructorWithMissingDeleteStatementsProblemId);
        C_164_AvoidConversionOperators = registry.createAndRegisterRule("C.164", this, CppCoreIdHelper.AvoidConversionOperatorsProblemId,
                CoreIdHelper.AvoidConversionOperatorsSharedProblemId);

        ES_020_AlwaysInitializeAnObject = registry.createAndRegisterRule("ES.20", this, CppCoreIdHelper.AlwaysInitializeAnObjectProblemId,
                CoreIdHelper.AlwaysInitializeAnObjectSharedProblemId);
        ES_026_DontUseVariableForTwoUnrelatedPurposes = registry.createAndRegisterRule("ES.26", this,
                CppCoreIdHelper.DontUseVariableForTwoUnrelatedPurposesProblemId);
        ES_046_AvoidLossyConversions = registry.createAndRegisterRule("ES.46", this, CppCoreIdHelper.AvoidLossyConversionsProblemId,
                CoreIdHelper.AvoidLossyConversionsSharedProblemId);
        ES_074_DeclareLoopVariableInTheIntializer = registry.createAndRegisterRule("ES.74", this,
                CppCoreIdHelper.DeclareLoopVariableInTheIntializerProblemId, CoreIdHelper.DeclareLoopVariableInTheIntializerSharedProblemId);

        ES_074_DeclareLoopVariableInTheIntializer.addQuickFixes(new DeclareLoopVariableInTheInitializerQuickFix("ES.74: Add a variable declaration"));
    }

    @Override
    public String getName() {
        return "C++ Core Guideline";
    }

    @Override
    public String getId() {
        return CppCoreIdHelper.GuidelineId;
    }

    @Override
    public ISuppressionStrategy getSuppressionStrategy() {
        return suppressionStrategy;
    }
}
