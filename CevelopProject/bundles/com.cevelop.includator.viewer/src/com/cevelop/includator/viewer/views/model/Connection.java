package com.cevelop.includator.viewer.views.model;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.ZestStyles;


public class Connection<NodeT> extends GraphElement<GraphConnection> {

    private final Node<NodeT> start;
    private final Node<NodeT> end;

    public Connection(Node<NodeT> start, Node<NodeT> end) {
        super();
        this.start = start;
        this.end = end;
    }

    public Node<NodeT> getStart() {
        return start;
    }

    public Node<NodeT> getEnd() {
        return end;
    }

    @Override
    protected GraphConnection drawRepresentation(final Graph graphModel) {
        return new GraphConnection(graphModel, ZestStyles.CONNECTIONS_DIRECTED, start.getRepresentation(), end.getRepresentation());
    }

    @Override
    protected void clearRepresentation(Graph graphModel) {
        if (!representation.isDisposed()) {
            representation.dispose();
        }
    }
}
