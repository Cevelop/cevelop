package com.cevelop.charwars.utils.analyzers;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;


public class UEAnalyzer {

    public static IASTExpression getOperand(IASTNode node) {
        IASTUnaryExpression ue = (IASTUnaryExpression) node;
        return ue.getOperand();
    }

    public static boolean isDereferenceExpression(IASTNode node) {
        return isUnaryExpression(node, IASTUnaryExpression.op_star);
    }

    public static boolean isAddressOperatorExpression(IASTNode node) {
        return isUnaryExpression(node, IASTUnaryExpression.op_amper);
    }

    public static boolean isSizeofExpression(IASTNode node) {
        return isUnaryExpression(node, IASTUnaryExpression.op_sizeof);
    }

    public static boolean isLogicalNot(IASTNode node) {
        return isUnaryExpression(node, IASTUnaryExpression.op_not);
    }

    public static boolean isBracketExpression(IASTNode node) {
        return isUnaryExpression(node, IASTUnaryExpression.op_bracketedPrimary);
    }

    public static boolean isPrefixIncrementation(IASTNode node) {
        return isUnaryExpression(node, IASTUnaryExpression.op_prefixIncr);
    }

    public static boolean isPostfixIncrementation(IASTNode node) {
        return isUnaryExpression(node, IASTUnaryExpression.op_postFixIncr);
    }

    public static boolean isIncrementation(IASTNode node) {
        return isPrefixIncrementation(node) || isPostfixIncrementation(node);
    }

    private static boolean isUnaryExpression(IASTNode node, int operator) {
        if (node instanceof IASTUnaryExpression) {
            IASTUnaryExpression unaryExpression = (IASTUnaryExpression) node;
            return unaryExpression.getOperator() == operator;
        }
        return false;
    }
}
