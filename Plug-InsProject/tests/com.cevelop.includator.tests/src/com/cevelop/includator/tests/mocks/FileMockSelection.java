package com.cevelop.includator.tests.mocks;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;


public class FileMockSelection implements IStructuredSelection {

    private final IFile file;

    public FileMockSelection(IFile file) {
        this.file = file;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Object getFirstElement() {
        return file;
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
        return new IFile[] { file };
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List toList() {
        return Arrays.asList(toArray());
    }
}
