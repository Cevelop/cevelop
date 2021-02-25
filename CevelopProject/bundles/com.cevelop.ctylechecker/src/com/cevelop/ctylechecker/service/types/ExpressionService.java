package com.cevelop.ctylechecker.service.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.cevelop.ctylechecker.domain.ExpressionType;
import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.IGroupExpression;
import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.types.Expression;
import com.cevelop.ctylechecker.domain.types.ExpressionGroup;
import com.cevelop.ctylechecker.domain.types.util.Expressions;
import com.cevelop.ctylechecker.service.IExpressionService;


public class ExpressionService implements IExpressionService {

    @Override
    public Optional<ISingleExpression> find(String expressionName) {
        return Expressions.find(expressionName);
    }

    @Override
    public ISingleExpression createExpression(String pName, String pRegex, Boolean pShouldMatch) {
        return new Expression(pName, pRegex, pShouldMatch);
    }

    @Override
    public IGroupExpression createExpressionGroup(String pName, Boolean pShouldMatch) {
        return new ExpressionGroup(pName, pShouldMatch);
    }

    @Override
    public List<IExpression> getExpressions(IExpression pGroup) {
        if (pGroup.getType().equals(ExpressionType.GROUP)) {
            IGroupExpression group = (IGroupExpression) pGroup;
            return group.getExpressions();
        }
        return new ArrayList<>();
    }

    @Override
    public void addToExpressionGroup(IExpression pSource, IExpression pTarget) {
        if (pSource.getType().equals(ExpressionType.GROUP)) {
            IGroupExpression group = (IGroupExpression) pSource;
            group.addExpression(pTarget);
        }
    }

    @Override
    public List<ISingleExpression> getAll() {
        return Expressions.all();
    }

}
