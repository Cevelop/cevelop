package com.cevelop.codeanalysator.autosar.quickfix;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;


public class ParenthesizeLogicalOperatorsQuickFix extends BaseQuickFix {

    IASTBinaryExpression rootBinaryExpression;

    public ParenthesizeLogicalOperatorsQuickFix(String label) {
        super(label);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (markedNode instanceof IASTBinaryExpression) {
            IASTBinaryExpression binaryExpression = (IASTBinaryExpression) markedNode;
            IASTExpression expression = (IASTExpression) markedNode;
            while (expression.getParent() instanceof IASTExpression) {
                expression = (IASTExpression) expression.getParent();
                if (expression instanceof IASTBinaryExpression) {
                    binaryExpression = (IASTBinaryExpression) expression;
                }
            }
            IASTBinaryExpression replacement = addParenthesesToSubExpressions(binaryExpression.copy(), hRewrite);
            hRewrite.replace(binaryExpression, replacement, null);
        }
    }

    private IASTBinaryExpression addParenthesesToSubExpressions(IASTBinaryExpression binaryExpression, ASTRewrite hRewrite) {
        IASTExpression leftOperand = binaryExpression.getOperand1();
        IASTExpression rightOperand = binaryExpression.getOperand2();
        if (leftOperand instanceof IASTBinaryExpression) {
            leftOperand = addParenthesesToSubExpressions((IASTBinaryExpression) leftOperand, hRewrite);
            IASTUnaryExpression replacement = factory.newUnaryExpression(IASTUnaryExpression.op_bracketedPrimary, leftOperand.copy());
            binaryExpression.setOperand1(replacement);
        } else if (leftOperand instanceof IASTUnaryExpression) {
            IASTUnaryExpression unary = (IASTUnaryExpression) leftOperand;
            IASTExpression unaryOperand = unary.getOperand();
            if (unaryOperand instanceof IASTBinaryExpression) {
                unaryOperand = addParenthesesToSubExpressions((IASTBinaryExpression) unaryOperand, hRewrite);
            }
        }
        if (rightOperand instanceof IASTBinaryExpression) {
            rightOperand = addParenthesesToSubExpressions((IASTBinaryExpression) rightOperand, hRewrite);
            IASTUnaryExpression replacement = factory.newUnaryExpression(IASTUnaryExpression.op_bracketedPrimary, rightOperand.copy());
            binaryExpression.setOperand2(replacement);

        } else if (rightOperand instanceof IASTUnaryExpression) {
            IASTUnaryExpression unary = (IASTUnaryExpression) rightOperand;
            IASTExpression unaryOperand = unary.getOperand();
            if (unaryOperand instanceof IASTBinaryExpression) {
                unaryOperand = addParenthesesToSubExpressions((IASTBinaryExpression) unaryOperand, hRewrite);
            }
        }
        return binaryExpression;
    }
}
