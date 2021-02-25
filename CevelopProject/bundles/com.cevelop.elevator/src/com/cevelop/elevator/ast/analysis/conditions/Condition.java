package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTNode;


public abstract class Condition {

    public abstract boolean satifies(final IASTNode node);

    public Condition and(Condition cond) {
        return new And(this, cond);
    }

    public Condition or(Condition cond) {
        return new Or(this, cond);
    }

    public static Condition not(Condition cond) {
        return new Not(cond);
    }
}
