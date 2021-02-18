package com.cevelop.ctylechecker.domain.types;

import com.cevelop.ctylechecker.domain.IResolution;
import com.cevelop.ctylechecker.domain.ISingleExpression;


public abstract class AbstractRenameResolution implements IResolution {

    ISingleExpression expression;

    public AbstractRenameResolution(ISingleExpression expression) {
        this.expression = expression;
    }

    @Override
    public ISingleExpression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(ISingleExpression pExpression) {
        expression = pExpression;
    };
}
