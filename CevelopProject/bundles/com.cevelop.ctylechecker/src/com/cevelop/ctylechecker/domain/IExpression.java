package com.cevelop.ctylechecker.domain;

public interface IExpression extends ICtyleElement {

    void setHint(ResolutionHint hint);

    ResolutionHint getHint();

    ExpressionType getType();

    Boolean check(String pInput);

    Boolean isPrefered();
}
