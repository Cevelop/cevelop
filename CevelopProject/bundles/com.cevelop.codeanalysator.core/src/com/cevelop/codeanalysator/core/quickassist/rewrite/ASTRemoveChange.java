package com.cevelop.codeanalysator.core.quickassist.rewrite;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;


/**
 * Remove nodes from an AST.
 *
 * @author ythrier(at)hsr.ch
 */
public class ASTRemoveChange implements IASTChange {

    private final IASTNode node;

    /**
     * Create the remove change.
     *
     * @param node
     * Node to remove.
     */
    public ASTRemoveChange(IASTNode node) {
        this.node = node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ASTRewrite apply(ASTRewrite rewrite) {
        rewrite.remove(node, null);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASTNode getRewriteRoot() {
        return node.getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASTNode getChangeRoot() {
        return null;
    }
}
