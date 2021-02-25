package com.cevelop.ctylechecker.service.util;

import java.util.ArrayList;
import java.util.List;

import com.cevelop.ctylechecker.domain.ExpressionType;
import com.cevelop.ctylechecker.domain.IConcept;
import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.IGroupExpression;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.types.Concept;
import com.cevelop.ctylechecker.domain.types.Expression;
import com.cevelop.ctylechecker.domain.types.ExpressionGroup;
import com.cevelop.ctylechecker.domain.types.Rule;
import com.cevelop.ctylechecker.service.factory.ResolutionFactory;


public class StyleguideUtil {

    public static IRule makeRuleCopy(IRule rule) {
        IRule copyRule = new Rule(rule.getName(), rule.isEnabled());
        copyRule.setMessage(rule.getMessage());
        copyRule.setCheckedConcepts(makeConceptsCopy(rule));
        copyRule.setPredefinedExpressions(makeExpressionsCopy(rule.getPredefinedExpressions()));
        copyRule.setCustomExpressions(makeExpressionsCopy(rule.getCustomExpressions()));
        return copyRule;
    }

    public static List<IConcept> makeConceptsCopy(IRule rule) {
        List<IConcept> copyConcepts = new ArrayList<>();
        for (IConcept concept : rule.getCheckedConcepts()) {
            IConcept copyConcept = new Concept(concept.getType());
            List<String> copyQualifiers = new ArrayList<>();
            for (String qualifier : concept.getQualifiers()) {
                copyQualifiers.add(qualifier);
            }
            copyConcept.setQualifiers(copyQualifiers);
            copyConcepts.add(copyConcept);
        }
        return copyConcepts;
    }

    public static List<IExpression> makeExpressionsCopy(List<IExpression> pExpressionsToCopy) {
        List<IExpression> copyExpressions = new ArrayList<>();
        for (IExpression iExpression : pExpressionsToCopy) {
            copyExpressions.add(copyIExpressionHierarchy(iExpression));
        }
        return copyExpressions;
    }

    public static IExpression copyIExpressionHierarchy(IExpression iExpression) {
        if (iExpression.getType().equals(ExpressionType.SINGLE)) {
            return makeExpressionCopy(iExpression);
        } else {
            return makeExpressionGroupCopy(iExpression);
        }
    }

    public static IGroupExpression makeExpressionGroupCopy(IExpression iExpression) {
        ExpressionGroup group = (ExpressionGroup) iExpression;
        IGroupExpression copyGroup = new ExpressionGroup(group.getName(), group.shouldMatchAll());
        copyGroup.setHint(group.getHint());
        List<IExpression> copyExpressions = new ArrayList<>();
        for (IExpression lExpression : group.getExpressions()) {
            copyExpressions.add(copyIExpressionHierarchy(lExpression));
        }
        copyGroup.setExpressions(copyExpressions);
        return copyGroup;
    }

    public static IExpression makeExpressionCopy(IExpression iExpression) {
        ISingleExpression expression = (ISingleExpression) iExpression;
        ISingleExpression copyExpression = new Expression(expression.getName(), expression.getExpression(), expression.shouldMatch());
        copyExpression.setArgument(expression.getArgument());
        copyExpression.setHint(expression.getHint());
        copyExpression.setOrder(expression.getOrder());
        copyExpression.setResolution(ResolutionFactory.createResolution(ResolutionFactory.getType(expression.getResolution()), copyExpression));
        return copyExpression;
    }
}
