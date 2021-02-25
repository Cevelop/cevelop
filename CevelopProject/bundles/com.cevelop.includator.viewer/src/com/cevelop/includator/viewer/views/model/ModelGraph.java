package com.cevelop.includator.viewer.views.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphItem;


public class ModelGraph<T> {

    public ModelGraph(Graph displayGraph) {
        super();
        this.displayGraph = displayGraph;
    }

    private final ArrayList<Connection<T>> connections         = new ArrayList<>();
    private final HashMap<T, Node<T>>      nodes               = new HashMap<>();
    private final Graph                    displayGraph;
    private ArrayList<GraphItem>           elementsToHighlight = new ArrayList<>();

    public Node<T> createNode(String label, T data) {
        final Node<T> node = new Node<>(label, data);
        nodes.put(data, node);
        return node;
    }

    public Connection<T> createConnection(Node<T> start, Node<T> end) {
        final Connection<T> connection = new Connection<>(start, end);
        connections.add(connection);
        start.addConnection(connection);
        return connection;
    }

    public void draw() {
        drawItems(nodes.values());
        drawItems(connections);
        if (!displayGraph.isDisposed()) {
            displayGraph.applyLayout();
        }
    }

    private void drawItems(final Collection<? extends GraphElement<?>> elements) {
        if (displayGraph.isDisposed()) {
            return;
        }
        displayGraph.getDisplay().syncExec(() -> {
            for (GraphElement<?> element : elements) {
                element.draw(displayGraph);
            }
        });
    }

    public void clear() {
        clearItems(nodes.values());
        nodes.clear();
        clearItems(connections);
        connections.clear();
        if (!displayGraph.isDisposed()) {
            displayGraph.applyLayout();
        }
    }

    private void clearItems(final Collection<? extends GraphElement<?>> elements) {
        if (displayGraph.isDisposed()) {
            return;
        }
        displayGraph.getDisplay().syncExec(() -> {
            for (GraphElement<?> element : elements) {
                element.delete(displayGraph);
            }
        });
    }

    public Node<T> getNode(T nodeId) {
        return nodes.get(nodeId);
    }

    public boolean nodeExists(T nodeId) {
        return nodes.containsKey(nodeId);
    }

    public void highlight(GraphElement<? extends GraphItem> highlightedElement) {
        if (highlightedElement != null) {
            final GraphItem representation = highlightedElement.getRepresentation();
            elementsToHighlight.add(representation);
        }
    }

    public void highlightItems() {

        if (displayGraph.isDisposed()) {
            return;
        }

        displayGraph.getDisplay().syncExec(new Runnable() {

            @Override
            public void run() {
                clearHighlighting();
                displayGraph.setSelection(elementsToHighlight.toArray(new GraphItem[] {}));
            }

            private void clearHighlighting() {
                // Own implementation for clearing the selection, as the implementation of Graph throws exceptions on clearing disposed elements
                @SuppressWarnings("rawtypes")
                Iterator iterator = displayGraph.getSelection().iterator();
                while (iterator.hasNext()) {
                    GraphItem item = (GraphItem) iterator.next();
                    if (!item.isDisposed()) {
                        item.unhighlight();
                    }
                    iterator.remove();
                }
            }

        });

        elementsToHighlight.clear();
    }
}
