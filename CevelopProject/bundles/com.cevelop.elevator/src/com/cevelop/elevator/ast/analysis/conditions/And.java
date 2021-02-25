package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTNode;


public class And extends Condition {

    private final Condition p;
    private final Condition q;

    public And(final Condition p, final Condition q) {
        this.p = p;
        this.q = q;
    }

    @Override
    public boolean satifies(final IASTNode node) {
        return p.satifies(node) && q.satifies(node);
    }

}
