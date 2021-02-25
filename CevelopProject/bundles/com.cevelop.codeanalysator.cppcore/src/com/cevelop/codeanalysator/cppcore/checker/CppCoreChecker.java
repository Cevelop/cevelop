package com.cevelop.codeanalysator.cppcore.checker;

import java.util.ArrayList;
import java.util.Collection;

import com.cevelop.codeanalysator.core.checker.CodeAnalysatorChecker;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;
import com.cevelop.codeanalysator.core.visitor.shared.AlwaysInitializeAnObjectVisitor;
import com.cevelop.codeanalysator.core.visitor.shared.AvoidConversionOperatorsVisitor;
import com.cevelop.codeanalysator.core.visitor.shared.AvoidLossyArithmeticConversionsVisitor;
import com.cevelop.codeanalysator.core.visitor.shared.DeclareLoopVariableInTheInitializerVisitor;
import com.cevelop.codeanalysator.core.visitor.shared.MissingSpecialMemberFunctionsVisitor;
import com.cevelop.codeanalysator.core.visitor.shared.RedundantOperationsVisitor;
import com.cevelop.codeanalysator.cppcore.guideline.CppCoreGuideline;
import com.cevelop.codeanalysator.cppcore.visitor.DestructorHasNoBodyVisitor;
import com.cevelop.codeanalysator.cppcore.visitor.DestructorWithMissingDeleteStatementsVisitor;
import com.cevelop.codeanalysator.cppcore.visitor.DontUseVariableForTwoUnrelatedPurposesVisitor;
import com.cevelop.codeanalysator.cppcore.visitor.NoDestructorVisitor;


public class CppCoreChecker extends CodeAnalysatorChecker {

    @Override
    protected Collection<CodeAnalysatorVisitor> createVisitors(RuleReporter ruleReporter) {
        Collection<CodeAnalysatorVisitor> visitors = new ArrayList<>();
        visitors.add(new AvoidConversionOperatorsVisitor(CppCoreGuideline.C_164_AvoidConversionOperators, ruleReporter));
        visitors.add(new DeclareLoopVariableInTheInitializerVisitor(CppCoreGuideline.ES_074_DeclareLoopVariableInTheIntializer, ruleReporter));
        visitors.add(new RedundantOperationsVisitor(CppCoreGuideline.C_020_RedundantOperations, ruleReporter));
        visitors.add(new MissingSpecialMemberFunctionsVisitor(CppCoreGuideline.C_021_MissingSpecialMemberFunctions, ruleReporter));
        visitors.add(new AvoidLossyArithmeticConversionsVisitor(CppCoreGuideline.ES_046_AvoidLossyConversions, ruleReporter));
        visitors.add(new DestructorHasNoBodyVisitor(CppCoreGuideline.C_031_DestructorHasNoBody, ruleReporter));
        visitors.add(new NoDestructorVisitor(CppCoreGuideline.C_031_NoDestructor, ruleReporter));
        visitors.add(new DestructorWithMissingDeleteStatementsVisitor(CppCoreGuideline.C_031_DestructorWithMissingDeleteStatements, ruleReporter));
        visitors.add(new DontUseVariableForTwoUnrelatedPurposesVisitor(CppCoreGuideline.ES_026_DontUseVariableForTwoUnrelatedPurposes, ruleReporter));
        visitors.add(new AlwaysInitializeAnObjectVisitor(CppCoreGuideline.ES_020_AlwaysInitializeAnObject, ruleReporter));
        return visitors;
    }
}
