package com.cevelop.codeanalysator.misra.visitor;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBasicType;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class BoolExpressionOperandsInfoVisitor extends CodeAnalysatorVisitor {

    public BoolExpressionOperandsInfoVisitor(Rule rule, RuleReporter ruleReporter) {
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
        }

        return PROCESS_CONTINUE;
    }

    public void handleBinaryExpression(ICPPASTBinaryExpression expression) {
        int operator = expression.getOperator();

        if (operator != IASTBinaryExpression.op_equals && operator != IASTBinaryExpression.op_notequals) {
            return;
        }

        boolean isOperand1Bool = isBooleanType(expression.getOperand1());
        boolean isOperand2Bool = isBooleanType(expression.getOperand2());

        if (!isOperand1Bool && !isOperand2Bool) {
            return;
        }

        if (isLiteral(expression.getOperand2())) {
            reportRuleForNode(expression.getInitOperand2());
        }

        if (isLiteral(expression.getOperand1())) {
            reportRuleForNode(expression.getOperand1());
        }
    }

    private boolean isLiteral(IASTExpression expression) {
        if (!(expression instanceof IASTLiteralExpression)) {
            return false;
        }

        int kind = ((IASTLiteralExpression) expression).getKind();

        return kind == IASTLiteralExpression.lk_true || kind == IASTLiteralExpression.lk_false;
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
