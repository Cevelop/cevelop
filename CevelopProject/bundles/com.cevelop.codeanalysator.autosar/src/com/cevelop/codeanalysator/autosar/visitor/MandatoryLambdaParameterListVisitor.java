package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class MandatoryLambdaParameterListVisitor extends CodeAnalysatorVisitor {

    public MandatoryLambdaParameterListVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expr) {
        if (expr instanceof ICPPASTLambdaExpression) {
            ICPPASTLambdaExpression lambda = (ICPPASTLambdaExpression) expr;

            ICPPASTFunctionDeclarator declarator = lambda.getDeclarator();
            if (declarator == null) {
                reportRuleForNode(lambda);
            }
        }

        return super.visit(expr);
    }

}
