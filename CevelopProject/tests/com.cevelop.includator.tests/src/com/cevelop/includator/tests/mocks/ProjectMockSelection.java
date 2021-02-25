package com.cevelop.includator.tests.mocks;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.jface.viewers.IStructuredSelection;


public class ProjectMockSelection implements IStructuredSelection {

    private final ICProject cproject;

    public ProjectMockSelection(ICProject cproject) {
        this.cproject = cproject;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Object getFirstElement() {
        return cproject;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Iterator iterator() {
        return toList().iterator();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Object[] toArray() {
        return toList().toArray();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List toList() {
        return Arrays.asList(cproject);
    }

}
