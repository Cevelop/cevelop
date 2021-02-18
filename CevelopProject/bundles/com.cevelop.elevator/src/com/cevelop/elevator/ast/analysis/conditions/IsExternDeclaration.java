package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;


public class IsExternDeclaration extends Condition {

    @Override
    public boolean satifies(IASTNode node) {
        return isExternDeclaration(node);
    }

    private boolean isExternDeclaration(IASTNode node) {
        IASTNode parent = node.getParent();
        if (parent instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration declaration = ((IASTSimpleDeclaration) parent);
            return declaration.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_extern;
        }
        return false;
    }
}
