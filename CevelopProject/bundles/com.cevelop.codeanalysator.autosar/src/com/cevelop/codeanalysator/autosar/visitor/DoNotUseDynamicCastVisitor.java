package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCastExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCastExpression;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


@SuppressWarnings("restriction")
public class DoNotUseDynamicCastVisitor extends CodeAnalysatorVisitor {

    public DoNotUseDynamicCastVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expression) {
        if (expression instanceof CPPASTCastExpression) {
            CPPASTCastExpression cast = (CPPASTCastExpression) expression;
            if (violatesRule(cast)) {
                reportRuleForNode(expression);
            }
        }
        return super.visit(expression);
    }

    private boolean violatesRule(CPPASTCastExpression cast) {
        return cast.getOperator() == ICPPASTCastExpression.op_dynamic_cast;
    }
}
