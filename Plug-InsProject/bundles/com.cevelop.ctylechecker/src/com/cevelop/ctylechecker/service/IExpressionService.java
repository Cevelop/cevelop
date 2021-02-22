package com.cevelop.ctylechecker.service;

import java.util.List;
import java.util.Optional;

import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.IGroupExpression;
import com.cevelop.ctylechecker.domain.ISingleExpression;


public interface IExpressionService {

    Optional<ISingleExpression> find(String pExpressionName);

    ISingleExpression createExpression(String pName, String pRegex, Boolean pShouldMatch);

    IGroupExpression createExpressionGroup(String pName, Boolean pShouldMatch);

    void addToExpressionGroup(IExpression pSource, IExpression pTarget);

    List<IExpression> getExpressions(IExpression pGroup);

    List<ISingleExpression> getAll();
}
