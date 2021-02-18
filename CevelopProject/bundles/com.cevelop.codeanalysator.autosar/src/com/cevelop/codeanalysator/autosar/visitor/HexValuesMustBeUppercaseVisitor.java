package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;

import com.cevelop.codeanalysator.autosar.util.LiteralHelper;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class HexValuesMustBeUppercaseVisitor extends CodeAnalysatorVisitor {

    public HexValuesMustBeUppercaseVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expression) {
        if (expression instanceof IASTLiteralExpression) {
            if (LiteralHelper.isLowerCaseHex((IASTLiteralExpression) expression)) {
                reportRuleForNode(expression);
            }
        }
        return super.visit(expression);
    }
}
