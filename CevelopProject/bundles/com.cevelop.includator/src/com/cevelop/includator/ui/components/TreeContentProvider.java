/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.ui.components;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public abstract class TreeContentProvider<E> implements ITreeContentProvider {

    @SuppressWarnings("unchecked")
    @Override
    public E[] getChildren(Object parentElement) {
        if (parentElement instanceof IChildrenProvider<?>) {
            return ((IChildrenProvider<E>) parentElement).getChildren();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getParent(Object element) {
        if (element instanceof IParentProvider) {
            return ((IParentProvider<E>) element).getParent();
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof IChildrenProvider<?>) {
            return ((IChildrenProvider) element).hasChildren();
        }
        return false;
    }

    @Override
    public void dispose() {}

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

    @Override
    public abstract E[] getElements(Object inputElement);
}
