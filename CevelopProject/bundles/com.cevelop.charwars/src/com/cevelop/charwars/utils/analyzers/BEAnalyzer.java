package com.cevelop.charwars.utils.analyzers;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;


public class BEAnalyzer {

    public static IASTExpression getOperand1(IASTNode node) {
        IASTBinaryExpression be = (IASTBinaryExpression) node;
        return be.getOperand1();
    }

    public static IASTExpression getOperand2(IASTNode node) {
        IASTBinaryExpression be = (IASTBinaryExpression) node;
        return be.getOperand2();
    }

    public static IASTExpression getOtherOperand(IASTNode operand) {
        IASTBinaryExpression be = (IASTBinaryExpression) operand.getParent();
        return isOp1(operand) ? be.getOperand2() : be.getOperand1();
    }

    public static boolean isOp1(IASTNode operand) {
        IASTBinaryExpression be = (IASTBinaryExpression) operand.getParent();
        return be.getOperand1() == operand;
    }

    public static boolean isSubtraction(IASTNode node) {
        return isBinaryExpression(node, IASTBinaryExpression.op_minus);
    }

    public static boolean isAddition(IASTNode node) {
        return isBinaryExpression(node, IASTBinaryExpression.op_plus);
    }

    public static boolean isDivision(IASTNode node) {
        return isBinaryExpression(node, IASTBinaryExpression.op_divide);
    }

    public static boolean isLeftShiftExpression(IASTNode node) {
        return isBinaryExpression(node, IASTBinaryExpression.op_shiftLeft);
    }

    public static boolean isComparison(IASTNode node) {
        return isEqualityCheck(node) || isInequalityCheck(node);
    }

    public static boolean isComparison(IASTNode node, boolean equalityComparison) {
        return equalityComparison ? isEqualityCheck(node) : isInequalityCheck(node);
    }

    private static boolean isEqualityCheck(IASTNode node) {
        return isBinaryExpression(node, IASTBinaryExpression.op_equals);
    }

    private static boolean isInequalityCheck(IASTNode node) {
        return isBinaryExpression(node, IASTBinaryExpression.op_notequals);
    }

    public static boolean isAssignment(IASTNode node) {
        return isBinaryExpression(node, IASTBinaryExpression.op_assign);
    }

    public static boolean isPlusAssignment(IASTNode node) {
        return isBinaryExpression(node, IASTBinaryExpression.op_plusAssign);
    }

    public static boolean isLogicalAnd(IASTNode node) {
        return isBinaryExpression(node, IASTBinaryExpression.op_logicalAnd);
    }

    public static boolean isLogicalOr(IASTNode node) {
        return isBinaryExpression(node, IASTBinaryExpression.op_logicalOr);
    }

    private static boolean isBinaryExpression(IASTNode node, int operator) {
        if (node instanceof IASTBinaryExpression) {
            IASTBinaryExpression binaryExpression = (IASTBinaryExpression) node;
            return binaryExpression.getOperator() == operator;
        }
        return false;
    }
}
