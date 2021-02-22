package com.cevelop.codeanalysator.misra.checker;

import java.util.ArrayList;
import java.util.Collection;

import com.cevelop.codeanalysator.core.checker.CodeAnalysatorChecker;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;
import com.cevelop.codeanalysator.core.visitor.shared.AvoidLossyArithmeticConversionsVisitor;
import com.cevelop.codeanalysator.core.visitor.shared.DeclareLoopVariableInTheInitializerVisitor;
import com.cevelop.codeanalysator.misra.guideline.MisraGuideline;
import com.cevelop.codeanalysator.misra.visitor.BoolExpressionOperandsInfoVisitor;
import com.cevelop.codeanalysator.misra.visitor.BoolExpressionOperandsWarningVisitor;


public class MisraChecker extends CodeAnalysatorChecker {

    @Override
    protected Collection<CodeAnalysatorVisitor> createVisitors(RuleReporter ruleReporter) {
        Collection<CodeAnalysatorVisitor> visitors = new ArrayList<>();
        visitors.add(new DeclareLoopVariableInTheInitializerVisitor(MisraGuideline.M03_04_01_DeclareLoopVariableInTheIntializer, ruleReporter));
        visitors.add(new AvoidLossyArithmeticConversionsVisitor(MisraGuideline.M05_00_06_AvoidLossyConversion, ruleReporter));
        visitors.add(new BoolExpressionOperandsWarningVisitor(MisraGuideline.M04_05_01_BoolExpressionOperandsProblem, ruleReporter));
        visitors.add(new BoolExpressionOperandsInfoVisitor(MisraGuideline.M04_05_01_BoolExpressionOperandsInfo, ruleReporter));
        return visitors;
    }
}
