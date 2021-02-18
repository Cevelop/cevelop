package com.cevelop.ctylechecker.domain.types;

import java.util.ArrayList;
import java.util.List;

import com.cevelop.ctylechecker.domain.ExpressionType;
import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.IGroupExpression;
import com.cevelop.ctylechecker.domain.ResolutionHint;
import com.google.gson.annotations.Expose;


public class ExpressionGroup extends AbstractCtyleElement implements IGroupExpression {

    @Expose
    private List<IExpression> expressions;

    @Expose
    private ResolutionHint hint;

    public ExpressionGroup(String pName, Boolean pMatchAll) {
        this(pName, pMatchAll, new ArrayList<>());
    }

    public ExpressionGroup(String pName, Boolean pMatchAll, List<IExpression> pList) {
        super(pName, pMatchAll);
        expressions = pList;
        setHint(ResolutionHint.NONE);
    }

    public Boolean check(String pName) {
        if (shouldMatchAll()) {
            return checkMatchAll(pName);
        } else {
            return checkMatchAny(pName);
        }
    }

    private Boolean checkMatchAny(String pName) {
        for (IExpression expression : expressions) {
            if (expression.check(pName)) {
                return true;
            }
        }
        return false;
    }

    private Boolean checkMatchAll(String pName) {
        for (IExpression expression : expressions) {
            if (!expression.check(pName)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean containsPrefered() {
        for (IExpression expression : expressions) {
            if (isPrefered(expression)) {
                return true;
            }
        }
        return false;
    }

    public Boolean isPrefered() {
        return isPrefered(this);
    }

    private Boolean isPrefered(IExpression expression) {
        return expression.getHint().equals(ResolutionHint.PREFERED);
    }

    @Override
    public List<IExpression> getPrefered() {
        List<IExpression> prefered = new ArrayList<>();
        for (IExpression expression : expressions) {
            if (isPrefered(expression)) {
                prefered.add(expression);
            }
        }
        return prefered;
    }

    public void addExpression(IExpression pExpression) {
        this.expressions.add(pExpression);
    }

    @Override
    public List<IExpression> getExpressions() {
        return expressions;
    }

    @Override
    public void setExpressions(List<IExpression> pExpressions) {
        this.expressions = pExpressions;
    }

    public Boolean shouldMatchAll() {
        return isEnabled();
    }

    public void shouldMatchAll(Boolean pMatchAll) {
        isEnabled(pMatchAll);
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.GROUP;
    }

    public ResolutionHint getHint() {
        return hint;
    }

    public void setHint(ResolutionHint hint) {
        this.hint = hint;
    }
}
