package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression.CaptureDefault;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class DoNotImpliciltyCaptureVariablesVisitor extends CodeAnalysatorVisitor {

    public DoNotImpliciltyCaptureVariablesVisitor(Rule rule, RuleReporter ruleReporter) {
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

            if (lambdaExpression.getCaptureDefault() != CaptureDefault.UNSPECIFIED) {
                reportRuleForNode(lambdaExpression);
            }
        }

        return super.visit(expression);
    }

}
