package com.cevelop.elevator.ast.analysis;

import org.eclipse.cdt.core.dom.ast.IASTNode;


public class NodeProperties {

    private final IASTNode node;

    public NodeProperties(final IASTNode node) {
        this.node = node;
    }

    public <T extends IASTNode> boolean hasAncestor(Class<T> parent) {
        return getParent(node, parent) != null;
    }

    public <T extends IASTNode> int getDistanceToAncestor(Class<T> parent) {
        return getDistanceToAncestor(node, parent);
    }

    public <T> T getAncestor(Class<T> parent) {
        return getParent(node, parent);
    }

    @SuppressWarnings("unchecked")
    private <T> T getParent(IASTNode node, Class<T> parent) {
        return (T) (node == null || parent.isInstance(node) ? node : getParent(node.getParent(), parent));
    }

    private <T> int getDistanceToAncestor(IASTNode node, Class<T> parent) {
        return (node == null || parent.isInstance(node) ? 0 : 1 + getDistanceToAncestor(node.getParent(), parent));
    }
}
