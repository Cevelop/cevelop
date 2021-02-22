package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCatchHandler;


/**
 * Checks if node is part of an {@link ICPPASTCatchHandler}.
 */
public class IsCatchParameter extends Condition {

    @Override
    public boolean satifies(final IASTNode node) {
        return isCatchParameter(node);
    }

    private boolean isCatchParameter(final IASTNode node) {
        if (node instanceof ICPPASTCatchHandler) {
            return true;
        }

        if (node instanceof IASTSimpleDeclaration || node instanceof IASTDeclarator) {
            return isCatchParameter(node.getParent());
        }

        return false;
    }
}
