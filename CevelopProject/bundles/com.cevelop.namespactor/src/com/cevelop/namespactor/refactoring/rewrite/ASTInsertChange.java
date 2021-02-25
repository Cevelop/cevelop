package com.cevelop.namespactor.refactoring.rewrite;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;


/**
 * Insert nodes in an AST.
 *
 * @author ythrier(at)hsr.ch
 */
public class ASTInsertChange implements IASTChange {

    private final IASTNode insertionPoint;
    private final IASTNode newNode;
    private final IASTNode root;

    /**
     * Create the insert change.
     *
     * @param root
     * The root of the insert operation.
     * @param newNode
     * The new node to add.
     * @param insertionPoint
     * The insertion point.
     */
    public ASTInsertChange(IASTNode root, IASTNode newNode, IASTNode insertionPoint) {
        this.insertionPoint = insertionPoint;
        this.newNode = newNode;
        this.root = root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ASTRewrite apply(ASTRewrite rewrite) {
        return rewrite.insertBefore(root, insertionPoint, newNode, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASTNode getRewriteRoot() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASTNode getChangeRoot() {
        return newNode;
    }
}
