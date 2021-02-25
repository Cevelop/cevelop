package com.cevelop.ctylechecker.domain.types;

import com.google.gson.annotations.Expose;

import com.cevelop.ctylechecker.domain.ExpressionType;
import com.cevelop.ctylechecker.domain.IResolution;
import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.OrderPriority;
import com.cevelop.ctylechecker.domain.ResolutionHint;


public class Expression extends AbstractCtyleElement implements ISingleExpression {

    private static final Boolean MATCH = Boolean.TRUE;

    @Expose
    private OrderPriority order;

    @Expose
    private IResolution resolution;

    @Expose
    private String argument;

    @Expose
    private String expression;

    @Expose
    private ResolutionHint hint;

    public Expression(String pName) {
        this(pName, "", MATCH);
    }

    public Expression(String pName, String pExpression) {
        this(pName, pExpression, MATCH);
    }

    public Expression(String pName, String pExpression, Boolean pShouldMatch) {
        super(pName, pShouldMatch);
        setExpression(pExpression);
        shouldMatch(pShouldMatch);
        setOrder(OrderPriority.HIGH);
        setArgument("");
        setResolution(new DefaultRenameResolution());
        setHint(ResolutionHint.NONE);
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public void setExpression(String pExpression) {
        this.expression = pExpression;
    }

    @Override
    public Boolean isPrefered() {
        return getHint().equals(ResolutionHint.PREFERED);
    }

    @Override
    public Boolean shouldMatch() {
        return isEnabled();
    }

    @Override
    public void shouldMatch(Boolean pShouldMatch) {
        isEnabled(pShouldMatch);
    }

    @Override
    public Boolean check(String pInput) {
        if (shouldMatch()) {
            return pInput.matches(String.format(expression, argument));
        }
        return !pInput.matches(String.format(expression, argument));
    }

    @Override
    public OrderPriority getOrder() {
        if (order == null) {
            order = OrderPriority.HIGH;
        }
        return order;
    }

    @Override
    public void setOrder(OrderPriority order) {
        this.order = order;
    }

    @Override
    public String getArgument() {
        return argument;
    }

    @Override
    public void setArgument(String argument) {
        this.argument = argument;
    }

    @Override
    public IResolution getResolution() {
        return resolution;
    }

    @Override
    public void setResolution(IResolution resolution) {
        this.resolution = resolution;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.SINGLE;
    }

    @Override
    public ResolutionHint getHint() {
        return hint;
    }

    @Override
    public void setHint(ResolutionHint hint) {
        this.hint = hint;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Expression ? equals((ISingleExpression) obj) : false;
    }

    public boolean equals(ISingleExpression pExpression) {
        return getName().equals(pExpression.getName()) && getExpression().equals(pExpression.getExpression()) && isEnabled().equals(pExpression
                .isEnabled());
    }
}
