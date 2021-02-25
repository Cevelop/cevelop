package com.cevelop.templator.plugin.asttools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;


public class FindRelevantExpressionsInReturnVisitor extends ASTVisitor {

    private List<IASTExpression> relevantExpressions;

    public FindRelevantExpressionsInReturnVisitor() {
        shouldVisitStatements = true;
        relevantExpressions = new ArrayList<>();
    }

    @Override
    public int visit(IASTStatement statement) {
        if (statement instanceof IASTReturnStatement) {
            IASTReturnStatement returnStmtn = (IASTReturnStatement) statement;
            IASTExpression returnValue = returnStmtn.getReturnValue();
            if (isRelevant(returnValue)) {
                relevantExpressions.add(returnValue);
            }
        }
        return super.visit(statement);
    }

    private boolean isRelevant(IASTExpression expression) {
        return getRelevance(expression) >= 0;
    }

    private int getRelevance(IASTExpression expression) {
        if (expression instanceof ICPPASTSimpleTypeConstructorExpression) {
            return 1;
        } else if (expression instanceof IASTCastExpression) {
            return 3;
        } else if (expression instanceof IASTConditionalExpression) {
            return 5;
        } else if (expression instanceof IASTBinaryExpression) {
            return 7;
        } else if (expression instanceof IASTUnaryExpression) {
            return 9;
        } else if (expression instanceof ICPPASTFunctionCallExpression) {
            return 11;
        } else if (expression instanceof IASTIdExpression) {
            return 13;
        } else if (expression instanceof ICPPASTFieldReference) {
            return 15;
        } else if (expression instanceof ICPPASTArraySubscriptExpression) {
            return 17;
        }
        return -1;
    }

    public List<IASTExpression> getRelevanceExpressionsSorted() {
        relevantExpressions.sort(new RelevanceComparator());
        return relevantExpressions;
    }

    public IASTExpression getMostRelevantExpression() {
        if (relevantExpressions.isEmpty()) {
            return null;
        }
        return getRelevanceExpressionsSorted().get(0);
    }

    class RelevanceComparator implements Comparator<IASTExpression> {

        @Override
        public int compare(IASTExpression a, IASTExpression b) {
            return getRelevance(a) - getRelevance(b);
        }
    }
}
