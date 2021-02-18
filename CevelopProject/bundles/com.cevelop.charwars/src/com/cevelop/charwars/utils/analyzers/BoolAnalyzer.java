package com.cevelop.charwars.utils.analyzers;

import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IType;

import com.cevelop.charwars.constants.Constants;


public class BoolAnalyzer {

    public static IASTExpression getCondition(IASTNode node) {
        if (node instanceof IASTIfStatement) {
            IASTIfStatement ifStatement = (IASTIfStatement) node;
            return ifStatement.getConditionExpression();
        } else if (node instanceof IASTWhileStatement) {
            IASTWhileStatement whileStatement = (IASTWhileStatement) node;
            return whileStatement.getCondition();
        } else if (node instanceof IASTForStatement) {
            IASTForStatement forStatement = (IASTForStatement) node;
            return forStatement.getConditionExpression();
        } else if (node instanceof IASTConditionalExpression) {
            IASTConditionalExpression conditionalExpression = (IASTConditionalExpression) node;
            return conditionalExpression.getLogicalConditionExpression();
        } else if (node instanceof IASTDoStatement) {
            IASTDoStatement doStatement = (IASTDoStatement) node;
            return doStatement.getCondition();
        }
        return null;
    }

    public static IASTNode getEnclosingBoolean(IASTNode node) {
        while (node != null && !isImplicitBool(node)) {
            node = node.getParent();
        }
        return node;
    }

    public static boolean isImplicitBool(IASTNode node) {
        if (node == null) {
            return false;
        }

        IASTNode parent = node.getParent();
        boolean isCondition = isCondition(node);
        boolean isLogicalOperand = BEAnalyzer.isLogicalAnd(parent) || BEAnalyzer.isLogicalOr(parent);
        boolean isAssignedToBool = isAssignedToBool(node);
        boolean isAsserted = isAsserted(node);
        boolean isReturned = isReturned(node);
        return isCondition || isLogicalOperand | isAssignedToBool || isAsserted || isReturned;
    }

    public static boolean isCondition(IASTNode node) {
        IASTNode parent = node.getParent();
        IASTNode condition = BoolAnalyzer.getCondition(parent);
        return condition == node;
    }

    public static boolean isAssignedToBool(IASTNode node) {
        IASTNode parent = node.getParent();
        if (parent == null) {
            return false;
        }
        parent = parent.getOriginalNode();

        if (BEAnalyzer.isAssignment(parent)) {
            IType expressionType = BEAnalyzer.getOperand1(parent).getExpressionType();
            return TypeAnalyzer.getBasicKind(expressionType) == Kind.eBoolean;
        } else if (parent instanceof IASTEqualsInitializer && parent.getParent() instanceof IASTDeclarator) {
            IASTDeclarator declarator = (IASTDeclarator) parent.getParent();
            return DeclaratorTypeAnalyzer.hasBoolType(declarator);
        }
        return false;
    }

    public static boolean isAsserted(IASTNode node) {
        IASTNode parent = node.getParent();
        if (parent != null) {
            String rs = parent.getRawSignature();
            return rs.contains(Constants.ASSERT + "(") || rs.contains(Constants.ASSERT.toUpperCase() + "(");
        }
        return false;
    }

    private static boolean isReturned(IASTNode node) {
        return node.getParent() instanceof IASTReturnStatement;
    }
}
