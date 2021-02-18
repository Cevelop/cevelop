package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTNode;


public class Not extends Condition {

    private final Condition cond;

    public Not(final Condition cond) {
        this.cond = cond;
    }

    @Override
    public boolean satifies(final IASTNode node) {
        return !cond.satifies(node);
    }

}
