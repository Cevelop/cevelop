package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;


/**
 * Checks if the tested {@link IASTDeclarator} or {@link ICPPASTConstructorChainInitializer} are already
 * initialized using a {@link IASTInitializerList}.
 *
 */
public class IsElevated extends Condition {

    @Override
    public boolean satifies(final IASTNode node) {
        if (node instanceof ICPPASTConstructorChainInitializer) {
            return isElevated(((ICPPASTConstructorChainInitializer) node).getInitializer());
        } else if (node instanceof IASTDeclarator) {
            return isElevated(((IASTDeclarator) node).getInitializer());
        }
        return false;
    }

    private boolean isElevated(final IASTInitializer initializer) {
        return initializer != null && initializer instanceof IASTInitializerList;
    }
}
