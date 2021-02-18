package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTPackExpansionExpression;

import com.cevelop.elevator.ast.analysis.NodeProperties;


/**
 * Checks if the initializer contains a PackExpansionExpression.
 *
 * Initializers with PackExpansionExpressions have to be filtered out because
 * ASTWriter does not yet support them. As soon as this is fixed, they can be
 * included again.
 */
public class ContainsPackExpansion extends Condition {

    @Override
    public boolean satifies(final IASTNode node) {
        if (node instanceof IASTInitializer) {
            return containsPackExpansion((IASTInitializer) node);
        }
        if (node instanceof IASTDeclarator) {
            return containsPackExpansion((IASTDeclarator) node);
        }
        return false;
    }

    private boolean containsPackExpansion(final IASTInitializer initializer) {
        if (initializer instanceof ICPPASTConstructorInitializer) {
            for (final IASTInitializerClause clause : ((ICPPASTConstructorInitializer) initializer).getArguments()) {
                if (clause instanceof ICPPASTPackExpansionExpression) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsPackExpansion(final IASTDeclarator declarator) {
        final IASTInitializer initializer = declarator.getInitializer();
        NodeProperties properties = new NodeProperties(declarator);
        if (properties.hasAncestor(ICPPASTNewExpression.class)) {
            return containsPackExpansion(properties.getAncestor(ICPPASTNewExpression.class).getInitializer());
        }
        return containsPackExpansion(initializer);
    }

}
