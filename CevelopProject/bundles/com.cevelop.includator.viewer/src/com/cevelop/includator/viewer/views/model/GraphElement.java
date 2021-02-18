package com.cevelop.includator.viewer.views.model;

import org.eclipse.zest.core.widgets.Graph;


public abstract class GraphElement<T> {

    protected T representation;

    protected void draw(final Graph graphModel) {
        if (representation == null) {
            representation = drawRepresentation(graphModel);
        }
    }

    protected void delete(final Graph graphModel) {
        if (representation != null) {
            clearRepresentation(graphModel);
        }
        representation = null;
    }

    protected T getRepresentation() {
        return representation;
    }

    protected abstract T drawRepresentation(Graph graphModel);

    protected abstract void clearRepresentation(Graph graphModel);

}
