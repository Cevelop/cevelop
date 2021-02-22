package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTNode;


public class HasEqualsInitializer extends Condition {

    @Override
    public boolean satifies(IASTNode node) {
        if (node instanceof IASTDeclarator) {
            return satisfies((IASTDeclarator) node);
        }
        return false;
    }

    private boolean satisfies(IASTDeclarator node) {
        return node.getInitializer() instanceof IASTEqualsInitializer;
    }

}
