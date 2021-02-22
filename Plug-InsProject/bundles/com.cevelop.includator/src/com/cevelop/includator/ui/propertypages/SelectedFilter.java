package com.cevelop.includator.ui.propertypages;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


public class SelectedFilter extends ViewerFilter {

    private final Set<String> excludedItems;

    public SelectedFilter(final String[] excludedItems) {
        this.excludedItems = new HashSet<>(Arrays.asList(excludedItems));
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {

        final String representation = getRepresentation(element);
        return representation != null && !excludedItems.contains(representation);
    }

    protected String getRepresentation(Object element) {
        if (element instanceof ICElement) {
            return ((ICElement) element).getResource().getProjectRelativePath().toOSString();
        }
        return null;
    }

}
