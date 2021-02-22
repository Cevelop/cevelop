package com.cevelop.ctylechecker.quickfix.dynamic.refactoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.cevelop.ctylechecker.domain.ExpressionType;
import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.IGroupExpression;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.OrderPriority;


public abstract class AbstractRenameRefactoring {

    String transformedName;

    protected String applyTransformations(IRule pRule) {
        transformedName = "";
        Optional<String> oOriginalName = getOriginalName();
        if (oOriginalName.isPresent() && pRule != null) {
            transformedName = oOriginalName.get();
            executeTransformations(pRule, pRule.getPredefinedExpressions());
            executeTransformations(pRule, pRule.getCustomExpressions());
        }
        return transformedName;
    }

    private String executeTransformations(IRule pRule, List<IExpression> pExpressions) {
        if (rootContainsPrefered(pExpressions)) {
            pExpressions = getRootPrefered(pExpressions);
        }
        for (int i = 0; i < OrderPriority.values().length; i++) {
            for (IExpression iexpression : pExpressions) {
                executeTransformationsRecursive(iexpression, i);
            }
        }
        return transformedName;
    }

    private Boolean rootContainsPrefered(List<IExpression> pExpressions) {
        for (IExpression expression : pExpressions) {
            if (expression.isPrefered()) {
                return true;
            }
        }
        return false;
    }

    private List<IExpression> getRootPrefered(List<IExpression> pExpressions) {
        List<IExpression> prefered = new ArrayList<>();
        for (IExpression expression : pExpressions) {
            if (expression.isPrefered()) {
                prefered.add(expression);
            }
        }
        return prefered;
    }

    private void executeExpressionTransformation(ISingleExpression expression, int pOrder) {
        if (expression.getOrder().ordinal() == pOrder) {
            if (!expression.check(transformedName)) {
                transformedName = expression.getResolution().transform(transformedName);
            }
        }
    }

    private void executeTransformationsRecursive(IExpression iExpression, int pOrder) {
        if (iExpression.getType().equals(ExpressionType.SINGLE)) {
            executeExpressionTransformation((ISingleExpression) iExpression, pOrder);
        }
        if (pOrder == OrderPriority.HIGH.ordinal() && iExpression.getType().equals(ExpressionType.GROUP)) {
            executeExpressionGroupTransformation((IGroupExpression) iExpression);
        }
    }

    private void executeExpressionGroupTransformation(IGroupExpression expressionGroup) {
        if (!expressionGroup.check(transformedName)) {
            for (int j = 0; j < OrderPriority.values().length; j++) {
                if (expressionGroup.containsPrefered()) {
                    executePredefinedTransformations(expressionGroup, j);
                } else {
                    executeAllExpressionTransformations(expressionGroup, j);
                }
            }
        }
    }

    private void executeAllExpressionTransformations(IGroupExpression expressionGroup, int j) {
        for (IExpression expression : expressionGroup.getExpressions()) {
            executeTransformationsRecursive(expression, j);
        }
    }

    private void executePredefinedTransformations(IGroupExpression expressionGroup, int j) {
        for (IExpression expression : expressionGroup.getPrefered()) {
            executeTransformationsRecursive(expression, j);
        }
    }

    protected abstract Optional<String> getOriginalName();
}
