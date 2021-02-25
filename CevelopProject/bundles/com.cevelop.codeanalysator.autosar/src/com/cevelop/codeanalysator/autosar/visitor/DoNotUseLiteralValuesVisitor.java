package com.cevelop.codeanalysator.autosar.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class DoNotUseLiteralValuesVisitor extends CodeAnalysatorVisitor {

    private List<Integer> exceptedLiterals = new ArrayList<>();

    public DoNotUseLiteralValuesVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
        exceptedLiterals.add(IASTLiteralExpression.lk_false);
        exceptedLiterals.add(IASTLiteralExpression.lk_true);
        exceptedLiterals.add(IASTLiteralExpression.lk_this);
        exceptedLiterals.add(IASTLiteralExpression.lk_nullptr);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expression) {
        if (expression instanceof IASTLiteralExpression) {
            IASTLiteralExpression literalExpression = (IASTLiteralExpression) expression;
            IASTNode parent = literalExpression.getParent();
            if (!isExcepted(literalExpression) && isInForbiddenLiteralContext(parent)) {
                reportRuleForNode(expression);
            }
        }
        return super.visit(expression);
    }

    private boolean isExcepted(IASTLiteralExpression expression) {
        Integer literalType = expression.getKind();
        if (exceptedLiterals.contains(literalType)) {
            return true;
        }
        if (literalType == IASTLiteralExpression.lk_integer_constant) {
            try {
                Long value = Long.decode(expression.toString());
                return value == 0 || value == 1;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    private boolean isInForbiddenLiteralContext(IASTNode parent) {
        if (parent instanceof IASTInitializer) {
            return false;
        }
        if (parent instanceof ICPPASTTemplateId) {
            return false;
        }
        if (parent instanceof IASTEqualsInitializer) {
            return false;
        }
        if (parent instanceof ICPPASTInitializerList) {
            return false;
        }
        if (parent instanceof ICPPASTFieldReference) {
            return false;
        }
        if (parent instanceof IASTUnaryExpression) {
            return isInForbiddenLiteralContext(parent.getParent());
        }
        if (parent instanceof IASTFunctionCallExpression) {
            IASTFunctionCallExpression funcCall = (IASTFunctionCallExpression) parent;
            /*
             * For example return statements or exceptions
             */
            if (funcCall.getParent() instanceof IASTUnaryExpression) {
                return false;
            }
        }

        return true;
    }
}
