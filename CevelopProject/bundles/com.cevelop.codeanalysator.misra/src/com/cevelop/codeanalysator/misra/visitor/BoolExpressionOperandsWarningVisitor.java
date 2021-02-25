package com.cevelop.codeanalysator.misra.visitor;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBasicType;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class BoolExpressionOperandsWarningVisitor extends CodeAnalysatorVisitor {

    public BoolExpressionOperandsWarningVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        this.shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expression) {
        if (expression instanceof ICPPASTBinaryExpression) {
            handleBinaryExpression((ICPPASTBinaryExpression) expression);
        } else if (expression instanceof ICPPASTUnaryExpression) {
            handleUnaryExpression((ICPPASTUnaryExpression) expression);
        }

        return PROCESS_CONTINUE;
    }

    public void handleBinaryExpression(ICPPASTBinaryExpression expression) {
        int operator = expression.getOperator();

        if (operator == IASTBinaryExpression.op_assign || operator == IASTBinaryExpression.op_equals ||
            operator == IASTBinaryExpression.op_notequals || operator == IASTBinaryExpression.op_logicalAnd ||
            operator == IASTBinaryExpression.op_logicalOr) {
            return;
        }

        if (expression.getOperand2() == null) {
            return;
        }

        boolean isOperand1Bool = isBooleanType(expression.getOperand1());
        boolean isOperand2Bool = isBooleanType(expression.getOperand2());

        if (isOperand1Bool || isOperand2Bool) {
            reportRuleForNode(expression);
        }
    }

    public void handleUnaryExpression(ICPPASTUnaryExpression expression) {
        int operator = expression.getOperator();

        if (operator == IASTUnaryExpression.op_star || operator == IASTUnaryExpression.op_amper || operator == IASTUnaryExpression.op_not ||
            operator == IASTUnaryExpression.op_bracketedPrimary || operator == ICPPASTUnaryExpression.op_throw ||
            operator == ICPPASTUnaryExpression.op_typeid || operator == IASTUnaryExpression.op_sizeof || operator == IASTUnaryExpression.op_alignOf ||
            operator == IASTUnaryExpression.op_noexcept) {
            return;
        }

        if (isBooleanType(expression.getOperand())) {
            reportRuleForNode(expression);
        }
    }

    private boolean isBooleanType(IASTExpression expression) {
        IType type = expression.getExpressionType();

        if (type instanceof IQualifierType) {
            IQualifierType qualifierType = (IQualifierType) type;
            type = qualifierType.getType();
        }

        if (type instanceof ICPPBasicType) {
            ICPPBasicType basicType = (ICPPBasicType) type;
            return basicType.getKind() == Kind.eBoolean;
        }

        return false;
    }
}
