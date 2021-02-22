package com.cevelop.clonewar.transformation.util;

import org.eclipse.cdt.core.dom.ast.IASTNode;


/**
 * A key pair of two class types AST node and his parent.
 *
 * @author ythrier(at)hsr.ch
 */
public class ASTKeyPair {

    private Class<?> parent_;
    private Class<?> node_;

    /**
     * Create the key pair with the node class and parent class.
     *
     * @param node
     * Node.
     */
    public ASTKeyPair(IASTNode node) {
        this.parent_ = node.getParent().getClass();
        this.node_ = node.getClass();
    }

    /**
     * Create the key pair from given classes.
     *
     * @param parent
     * Parent class.
     * @param node
     * Node class.
     */
    public ASTKeyPair(Class<?> parent, Class<?> node) {
        this.parent_ = parent;
        this.node_ = node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (parent_.getCanonicalName().hashCode() + node_.getCanonicalName().hashCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ASTKeyPair) {
            ASTKeyPair rhs = (ASTKeyPair) obj;
            return (rhs.parent_.equals(parent_) && rhs.node_.equals(node_));
        }
        return false;
    }
}
