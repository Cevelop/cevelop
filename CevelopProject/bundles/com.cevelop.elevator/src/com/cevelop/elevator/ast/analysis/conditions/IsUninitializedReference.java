package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;


public class IsUninitializedReference extends Condition {

    private final Condition isReference = new IsReference();

    @Override
    public boolean satifies(IASTNode node) {
        return isReference.satifies(node) && noInitializer(node);
    }

    private boolean noInitializer(IASTNode node) {
        return node instanceof IASTDeclarator && ((IASTDeclarator) node).getInitializer() == null;
    }

}
