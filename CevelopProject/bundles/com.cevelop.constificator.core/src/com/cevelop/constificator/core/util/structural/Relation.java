package com.cevelop.constificator.core.util.structural;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTNode;


public class Relation {

    @SuppressWarnings("unchecked")
    public static <T> T getAncestorOf(Class<T> dummy, IASTNode node) {
        while (node != null && !(dummy.isInstance(node))) {
            node = node.getParent();
        }

        return (T) node;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getDescendendOf(Class<T> dummy, IASTNode node) {
        while (node != null && !(dummy.isInstance(node))) {
            if (node.getChildren() != null && node.getChildren().length > 0) {
                for (IASTNode child : node.getChildren()) {
                    if (!dummy.isInstance(node)) {
                        node = (IASTNode) Relation.getDescendendOf(dummy, child);
                    } else {
                        break;
                    }
                }
            } else {
                node = null;
            }
        }

        return (T) node;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getDescendendsOf(Class<T> dummy, IASTNode node) {
        IASTNode[] children = node.getChildren();
        List<T> descendends = new ArrayList<>();

        if (dummy.isInstance(node)) {
            descendends.add((T) node);
        }

        for (IASTNode child : children) {
            if (dummy.isInstance(child)) {
                descendends.add((T) child);
            }

            for (IASTNode grandchild : child.getChildren()) {
                descendends.addAll(Relation.getDescendendsOf(dummy, grandchild));
            }
        }

        return descendends;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getGrandparentOf(Class<T> cls, IASTNode node) {
        if (cls.isInstance(node.getParent().getParent())) {
            node = node.getParent().getParent();
            return (T) node;
        }

        return null;
    }

    public static <T> boolean isDescendendOf(Class<T> dummy, IASTNode node) {
        return Relation.getAncestorOf(dummy, node) != null;
    }

}
