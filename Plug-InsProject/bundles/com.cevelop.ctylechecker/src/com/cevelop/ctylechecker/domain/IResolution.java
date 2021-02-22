package com.cevelop.ctylechecker.domain;

/**
 * @author Zmote
 * Interface for resolution used in the Ctylechecker
 */
public interface IResolution {

    ISingleExpression getExpression();

    void setExpression(ISingleExpression pExpression);

    abstract String transform(String pName);
}
