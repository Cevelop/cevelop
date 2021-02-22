package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class ParenthesizeLogicalOperatorsVisitor extends CodeAnalysatorVisitor {

    public ParenthesizeLogicalOperatorsVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expression) {
        if (expression instanceof IASTBinaryExpression) {
            IASTBinaryExpression binaryExpression = (IASTBinaryExpression) expression;
            if (violatesRule(binaryExpression)) {
                reportRuleForNode(expression);
            }
        }
        return super.visit(expression);
    }

    private boolean violatesRule(IASTBinaryExpression binaryExpression) {
        int operator = binaryExpression.getOperator();
        if (operator != IASTBinaryExpression.op_logicalAnd && operator != IASTBinaryExpression.op_logicalOr) {
            return false;
        }
        IASTExpression leftOperand = binaryExpression.getOperand1();
        IASTExpression rightOperand = binaryExpression.getOperand2();
        if (leftOperand instanceof IASTBinaryExpression || rightOperand instanceof IASTBinaryExpression) {
            return true;
        }
        return false;
    }
}
