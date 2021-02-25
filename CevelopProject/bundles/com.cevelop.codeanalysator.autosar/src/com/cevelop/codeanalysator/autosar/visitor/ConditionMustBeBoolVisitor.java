package com.cevelop.codeanalysator.autosar.visitor;

import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.ISemanticProblem;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class ConditionMustBeBoolVisitor extends CodeAnalysatorVisitor {

    private static final String CONVERSION_TO_BOOL_OPERATOR_NAME = "operator bool";

    public ConditionMustBeBoolVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitStatements = true;
    }

    @Override
    public int visit(IASTStatement statement) {
        IASTExpression conditionExpression = getConditionExpression(statement);
        checkConditionExpression(conditionExpression);
        return super.visit(statement);
    }

    private IASTExpression getConditionExpression(IASTStatement statement) {
        if (statement instanceof IASTIfStatement) {
            return ((IASTIfStatement) statement).getConditionExpression();
        } else if (statement instanceof IASTWhileStatement) {
            return ((IASTWhileStatement) statement).getCondition();
        } else if (statement instanceof IASTDoStatement) {
            return ((IASTDoStatement) statement).getCondition();
        } else if (statement instanceof IASTForStatement) {
            return ((IASTForStatement) statement).getConditionExpression();
        } else {
            return null;
        }
    }

    private boolean checkConditionExpression(IASTExpression conditionExpression) {
        if (conditionExpression == null) {
            return false;
        }

        conditionExpression = skipParentheses(conditionExpression);

        /*
         * Some binary expressions (logical and & or) convert the operands to bool which is not allowed
         * if there is not an explicit bool conversion operator, so the expression has to be checked recursively
         */
        if (conditionExpression instanceof IASTBinaryExpression) {
            IASTBinaryExpression binaryExpression = (IASTBinaryExpression) conditionExpression;
            int binaryOperator = binaryExpression.getOperator();
            if (binaryOperator == IASTBinaryExpression.op_logicalAnd || binaryOperator == IASTBinaryExpression.op_logicalOr) {
                boolean isOperand1Reported = checkConditionExpression(binaryExpression.getOperand1());
                boolean isOperand2Reported = checkConditionExpression(binaryExpression.getOperand2());
                if (isOperand1Reported || isOperand2Reported) {
                    return true;
                }
            }
        }

        if (isExpressionContextuallyConvertibleToBool(conditionExpression)) {
            return false;
        } else {
            reportRuleForNode(conditionExpression);
            return true;
        }
    }

    private IASTExpression skipParentheses(IASTExpression expression) {
        if (expression instanceof IASTUnaryExpression) {
            IASTUnaryExpression unaryExpression = (IASTUnaryExpression) expression;
            if (unaryExpression.getOperator() == IASTUnaryExpression.op_bracketedPrimary) {
                return skipParentheses(unaryExpression.getOperand());
            }
        }
        return expression;
    }

    @SuppressWarnings("restriction")
    private boolean isExpressionContextuallyConvertibleToBool(IASTExpression expression) {
        IType expressionType = expression.getExpressionType();
        expressionType = SemanticUtil.getNestedType(expressionType, SemanticUtil.TDEF);
        if (expressionType instanceof ISemanticProblem) {
            return true;
        } else if (expressionType instanceof IBasicType) {
            IBasicType basicType = (IBasicType) expressionType;
            return basicType.getKind() == Kind.eBoolean;
        } else if (expressionType instanceof ICPPClassType) {
            ICPPClassType classType = (ICPPClassType) expressionType;
            return Arrays.stream(classType.getMethods()) //
                    .anyMatch(this::isExplicitConversionOperatorToBool);
        } else {
            return false;
        }
    }

    private boolean isExplicitConversionOperatorToBool(ICPPMethod method) {
        return method.isExplicit() && CONVERSION_TO_BOOL_OPERATOR_NAME.equals(method.getName());
    }
}
