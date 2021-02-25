package com.cevelop.codeanalysator.core.quickassist.rewrite;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;


/**
 * Replace nodes in an AST.
 *
 * @author ythrier(at)hsr.ch
 */
public class ASTReplaceChange implements IASTChange {

    private final IASTNode replacement;
    private final IASTNode node;

    /**
     * Create the replace change.
     *
     * @param node
     * Node to replace.
     * @param replacement
     * The replacement of the node to replace.
     */
    public ASTReplaceChange(IASTNode node, IASTNode replacement) {
        this.node = node;
        this.replacement = replacement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ASTRewrite apply(ASTRewrite rewrite) {
        return rewrite.replace(node, replacement, null);
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
        return replacement;
    }
}
