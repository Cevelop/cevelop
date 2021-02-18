package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class DoNotUseTernaryConditionalOperatorAsSubExpressionVisitor extends CodeAnalysatorVisitor {

    public DoNotUseTernaryConditionalOperatorAsSubExpressionVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expression) {
        if (expression instanceof IASTConditionalExpression) {
            IASTConditionalExpression conditionalExpression = (IASTConditionalExpression) expression;

            IASTNode parent = conditionalExpression.getParent();
            if (parent instanceof IASTUnaryExpression) {
                IASTUnaryExpression unaryExpression = (IASTUnaryExpression) parent;
                if (unaryExpression.getOperator() == IASTUnaryExpression.op_bracketedPrimary) {
                    parent = parent.getParent();
                }
            }
            if (parent instanceof IASTExpression && isNotAssignment(parent)) {
                reportRuleForNode(conditionalExpression);
            }
        }

        return super.visit(expression);
    }

    private boolean isNotAssignment(IASTNode node) {
        if (node instanceof IASTBinaryExpression) {
            IASTBinaryExpression binaryExpression = (IASTBinaryExpression) node;
            switch (binaryExpression.getOperator()) {
            case IASTBinaryExpression.op_assign:
            case IASTBinaryExpression.op_binaryAndAssign:
            case IASTBinaryExpression.op_binaryOrAssign:
            case IASTBinaryExpression.op_binaryXorAssign:
            case IASTBinaryExpression.op_divideAssign:
            case IASTBinaryExpression.op_minusAssign:
            case IASTBinaryExpression.op_moduloAssign:
            case IASTBinaryExpression.op_multiplyAssign:
            case IASTBinaryExpression.op_plusAssign:
            case IASTBinaryExpression.op_shiftLeftAssign:
            case IASTBinaryExpression.op_shiftRightAssign:
                return false;
            }
        }
        return true;
    }
}
