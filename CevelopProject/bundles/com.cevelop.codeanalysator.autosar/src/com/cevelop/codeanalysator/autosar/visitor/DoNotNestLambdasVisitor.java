package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;

import com.cevelop.codeanalysator.autosar.util.LambdaHelper;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class DoNotNestLambdasVisitor extends CodeAnalysatorVisitor {

    public DoNotNestLambdasVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expression) {
        if (expression instanceof ICPPASTLambdaExpression) {
            ICPPASTLambdaExpression lambdaExpression = (ICPPASTLambdaExpression) expression;

            if (LambdaHelper.getEnclosingLambda(lambdaExpression) != null) {
                reportRuleForNode(lambdaExpression);
            }
        }

        return super.visit(expression);
    }

}
