package com.cevelop.includator.ui.propertypages;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


final class TypeFilter extends ViewerFilter {

    private final Class<?>[] acceptedTypes;

    TypeFilter(Class<?>[] acceptedTypes) {

        this.acceptedTypes = acceptedTypes;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {

        for (Class<?> acceptedType : acceptedTypes) {
            if (acceptedType.isInstance(element)) return true;
        }
        return false;
    }
}
