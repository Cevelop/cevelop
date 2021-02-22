package com.cevelop.ctylechecker.domain;

import java.util.List;


public interface IGroupExpression extends IExpression {

    void addExpression(IExpression camelCase);

    void setExpressions(List<IExpression> pExpressions);

    List<IExpression> getExpressions();

    Boolean containsPrefered();

    List<IExpression> getPrefered();
}
