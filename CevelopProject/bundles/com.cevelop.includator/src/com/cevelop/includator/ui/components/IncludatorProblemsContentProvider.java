package com.cevelop.includator.ui.components;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class IncludatorProblemsContentProvider implements ITreeContentProvider {

    private IStatus stati;

    @Override
    public void dispose() {}

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.stati = (MultiStatus) newInput;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return createWrappers(stati.getChildren());
    }

    private Object[] createWrappers(IStatus[] children) {
        Object[] result = new Object[children.length];
        for (int i = 0; i < children.length; i++) {
            result[i] = new StatusTreeItem(children[i]);
        }
        return result;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        return createWrappers(((StatusTreeItem) parentElement).getChildren());
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return ((StatusTreeItem) element).getChildren().length != 0;
    }
}



class StatusTreeItem {

    private final IStatus status;

    public StatusTreeItem(IStatus status) {
        this.status = status;
    }

    public IStatus[] getChildren() {
        return status.getChildren();
    }

    @Override
    public String toString() {
        return status.getMessage();
    }

    public IStatus getStatus() {
        return status;
    }
}
