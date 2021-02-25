package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTNode;


public class IsInstanceOf extends Condition {

    private final Class<?> instance;

    public IsInstanceOf(final Class<?> instance) {
        this.instance = instance;
    }

    @Override
    public boolean satifies(final IASTNode node) {
        return instance.isInstance(node);
    }

}
