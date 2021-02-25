package com.cevelop.includator.viewer.views.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;


public class Node<T> extends GraphElement<GraphNode> {

    private final String                   label;
    private final T                        data;
    private final ArrayList<Connection<T>> connections = new ArrayList<>();

    public Node(String label, T data) {
        super();
        this.label = label;
        this.data = data;
    }

    @Override
    protected GraphNode drawRepresentation(Graph graphModel) {
        return new GraphNode(graphModel, ZestStyles.NODES_NO_LAYOUT_RESIZE, label);
    }

    public String getLabel() {
        return label;
    }

    public T getData() {
        return data;
    }

    void addConnection(Connection<T> connection) {
        connections.add(connection);
    }

    public List<Connection<T>> getConnections() {
        return Collections.unmodifiableList(connections);
    }

    @Override
    protected void clearRepresentation(Graph graphModel) {
        if (!representation.isDisposed()) {
            representation.dispose();
        }
    }
}
