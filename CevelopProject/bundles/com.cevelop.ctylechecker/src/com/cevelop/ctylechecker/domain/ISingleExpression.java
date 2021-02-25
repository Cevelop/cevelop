package com.cevelop.ctylechecker.domain;

public interface ISingleExpression extends IExpression {

    void setResolution(IResolution resolution);

    IResolution getResolution();

    void setArgument(String argument);

    String getArgument();

    void setOrder(OrderPriority order);

    OrderPriority getOrder();

    void shouldMatch(Boolean pShouldMatch);

    Boolean shouldMatch();

    void setExpression(String pExpression);

    String getExpression();
}
