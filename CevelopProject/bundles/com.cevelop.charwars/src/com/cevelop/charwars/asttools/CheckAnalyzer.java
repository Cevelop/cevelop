package com.cevelop.charwars.asttools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.utils.analyzers.BEAnalyzer;
import com.cevelop.charwars.utils.analyzers.BoolAnalyzer;
import com.cevelop.charwars.utils.analyzers.FunctionAnalyzer;
import com.cevelop.charwars.utils.analyzers.LiteralAnalyzer;
import com.cevelop.charwars.utils.analyzers.UEAnalyzer;


public class CheckAnalyzer {

    public static IASTStatement[] getNullCheckedStatements(IASTName name, IASTStatement[] statements) {
        List<IASTStatement> nullCheckedStatements = new ArrayList<>();
        IASTIfStatement nullCheck = findNullCheck(name, statements);
        IASTNode guardClause = findGuardClause(name, statements);

        if (nullCheck != null) {
            IASTStatement nullCheckClause = getNullCheckClause(name, statements);
            if (nullCheckClause instanceof IASTCompoundStatement) {
                IASTCompoundStatement compoundClause = (IASTCompoundStatement) nullCheckClause;
                for (IASTStatement statement : compoundClause.getStatements()) {
                    nullCheckedStatements.add(statement);
                }
            } else {
                nullCheckedStatements.add(nullCheckClause);
            }
        } else if (guardClause != null) {
            int guardClauseIndex = Arrays.asList(statements).indexOf(guardClause);
            for (int i = guardClauseIndex + 1; i < statements.length; ++i) {
                nullCheckedStatements.add(statements[i]);
            }
        }
        return nullCheckedStatements.toArray(new IASTStatement[0]);
    }

    public static IASTIfStatement findNullCheck(IASTName name, IASTStatement[] statements) {
        IASTIfStatement nullCheck = null;
        for (IASTStatement statement : statements) {
            if (hasThenClauseWithNonNullString(statement, name) || hasElseClauseWithNonNullString(statement, name)) {
                nullCheck = (IASTIfStatement) statement;
                break;
            }
        }
        return nullCheck;
    }

    public static IASTStatement findGuardClause(IASTName name, IASTStatement[] statements) {
        for (IASTStatement statement : statements) {
            if (isNullComparison(statement, name, true)) {
                IASTIfStatement ifStatement = (IASTIfStatement) statement;
                IASTStatement thenClause = ifStatement.getThenClause();
                IASTStatement lastStatement = thenClause;
                if (thenClause instanceof IASTCompoundStatement) {
                    IASTCompoundStatement compoundThenClause = (IASTCompoundStatement) thenClause;
                    IASTStatement thenClauseStatements[] = compoundThenClause.getStatements();
                    lastStatement = thenClauseStatements[thenClauseStatements.length - 1];
                }

                if (lastStatement instanceof IASTReturnStatement && ifStatement.getElseClause() == null) {
                    return ifStatement;
                }
            } else if (isAssertStatement(statement, name)) {
                return statement;
            }
        }

        return null;
    }

    public static IASTStatement getNullCheckClause(IASTName name, IASTStatement[] statements) {
        IASTStatement nullCheckClause = null;
        IASTIfStatement nullCheck = findNullCheck(name, statements);
        if (nullCheck != null) {
            if (hasThenClauseWithNonNullString(nullCheck, name)) {
                nullCheckClause = nullCheck.getThenClause();
            } else {
                nullCheckClause = nullCheck.getElseClause();
            }
        }
        return nullCheckClause;
    }

    private static boolean hasThenClauseWithNonNullString(IASTStatement statement, IASTName strName) {
        if (!(statement instanceof IASTIfStatement)) {
            return false;
        }

        return isNullComparison(statement, strName, false);
    }

    private static boolean hasElseClauseWithNonNullString(IASTStatement statement, IASTName strName) {
        if (!(statement instanceof IASTIfStatement)) {
            return false;
        }

        boolean isNullComparison = isNullComparison(statement, strName, true);
        boolean hasElseClause = ((IASTIfStatement) statement).getElseClause() != null;
        return isNullComparison && hasElseClause;
    }

    private static boolean isNullComparison(IASTStatement statement, IASTName name, boolean equalCheck) {
        if (statement instanceof IASTIfStatement) {
            IASTIfStatement ifStatement = (IASTIfStatement) statement;
            IASTExpression ifCondition = ifStatement.getConditionExpression();

            IASTIdExpression idExpression = findFirstIdExpression(name, ifCondition);
            if (idExpression == null) return false;

            IASTNode check = idExpression.getParent();
            if ((check == ifStatement || BEAnalyzer.isLogicalAnd(check)) && !equalCheck) {
                return true;
            }

            if (!isNodeComparedToNull(idExpression, equalCheck)) return false;
            if (check == ifCondition) return true;

            IASTNode checkParent = check.getParent();
            if (checkParent != ifCondition) return false;
            return equalCheck ? BEAnalyzer.isLogicalOr(checkParent) : BEAnalyzer.isLogicalAnd(checkParent);
        }
        return false;
    }

    private static boolean isAssertStatement(IASTStatement statement, IASTName name) {
        if (statement instanceof IASTExpressionStatement) {
            IASTExpressionStatement expressionStatement = (IASTExpressionStatement) statement;
            IASTExpression expression = expressionStatement.getExpression();

            IASTIdExpression idExpression = findFirstIdExpression(name, expression);
            if (idExpression == null) return false;

            if (isNodeComparedToNull(idExpression, false)) {
                IASTNode booleanExpression = BoolAnalyzer.getEnclosingBoolean(idExpression);
                return BoolAnalyzer.isAsserted(booleanExpression);
            }
        }
        return false;
    }

    private static IASTIdExpression findFirstIdExpression(IASTName name, IASTNode node) {
        if (node instanceof IASTIdExpression) {
            IASTIdExpression idExpression = (IASTIdExpression) node;
            if (ASTAnalyzer.isSameName(idExpression.getName(), name)) {
                return idExpression;
            }
        }

        for (IASTNode child : node.getChildren()) {
            IASTIdExpression result = findFirstIdExpression(name, child);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static boolean isCheckedForEmptiness(IASTNode node, boolean empty) {
        IASTNode parent = node.getParent();
        return UEAnalyzer.isDereferenceExpression(parent) && isNodeComparedToZero(parent, empty);
    }

    public static boolean isPartOfStringCheck(IASTIdExpression node, boolean equalityComparison) {
        IASTNode parent = node.getParent();
        boolean isStrcmpOrWcsmp = FunctionAnalyzer.isCallToFunction(parent, Function.STRCMP) || FunctionAnalyzer.isCallToFunction(parent,
                Function.WCSCMP);
        return isStrcmpOrWcsmp && isNodeComparedToZero(parent, equalityComparison);
    }

    public static boolean isNodeComparedToNull(IASTNode node) {
        return isNodeComparedToNull(node, true) || isNodeComparedToNull(node, false);
    }

    public static boolean isNodeComparedToNull(IASTNode node, boolean equalityComparison) {
        return isNodeComparedTo(node, equalityComparison, false);
    }

    public static boolean isNodeComparedToZero(IASTNode node, boolean equalityComparison) {
        return isNodeComparedTo(node, equalityComparison, true);
    }

    private static boolean isNodeComparedTo(IASTNode node, boolean equalityComparison, boolean zeroOnly) {
        IASTNode parent = node.getParent();
        if (BEAnalyzer.isComparison(parent, equalityComparison)) {
            IASTNode oo = BEAnalyzer.getOtherOperand(node);
            return zeroOnly ? LiteralAnalyzer.isZero(oo) : LiteralAnalyzer.isNullExpression(oo);
        }

        if (equalityComparison) {
            return UEAnalyzer.isLogicalNot(parent);
        } else {
            return !UEAnalyzer.isLogicalNot(parent) && BoolAnalyzer.isImplicitBool(node);
        }
    }

    public static boolean isNodeComparedToStrlen(IASTNode node) {
        return isNodeComparedToStrlen(node, true) || isNodeComparedToStrlen(node, false);
    }

    public static boolean isNodeComparedToStrlen(IASTNode node, boolean equalityComparison) {
        IASTNode parent = node.getParent();
        if (parent instanceof IASTBinaryExpression) {
            IASTBinaryExpression comparison = (IASTBinaryExpression) parent;
            IASTExpression operand = BEAnalyzer.getOtherOperand(node);
            boolean isStrlenOperand = FunctionAnalyzer.isCallToFunction(operand, Function.STRLEN) || FunctionAnalyzer.isCallToMemberFunction(operand,
                    Function.SIZE);
            if (!isStrlenOperand) return false;

            int operator = comparison.getOperator();
            if (equalityComparison) {
                return operator == IASTBinaryExpression.op_equals;
            } else {
                if (operator == IASTBinaryExpression.op_notequals) return true;
                return operator == (BEAnalyzer.isOp1(node) ? IASTBinaryExpression.op_lessThan : IASTBinaryExpression.op_greaterThan);
            }
        }
        return false;
    }
}
