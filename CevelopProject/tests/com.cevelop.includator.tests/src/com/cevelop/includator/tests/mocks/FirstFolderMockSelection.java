package com.cevelop.includator.tests.mocks;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.cevelop.includator.helpers.IncludatorException;


public class FirstFolderMockSelection implements IStructuredSelection {

    private ICContainer folder;

    public FirstFolderMockSelection(ICProject project) {
        initFirstFolder(project);
    }

    private void initFirstFolder(ICProject project) {
        ICElementVisitor cElementVisitor = element -> {
            if (folder != null || element.getResource().getName().startsWith(".")) {
                return false;
            }
            switch (element.getElementType()) {
            case ICElement.C_CCONTAINER:
                if (!(element instanceof ISourceRoot)) {
                    folder = (ICContainer) element;
                    return false;
                }
                break;
            }
            return true;
        };
        try {
            project.accept(cElementVisitor);
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Object getFirstElement() {
        return folder;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Iterator iterator() {
        return toList().iterator();
    }

    @Override
    public int size() {
        return toList().size();
    }

    @Override
    public Object[] toArray() {
        return toList().toArray();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List toList() {
        return Arrays.asList(folder);
    }
}
