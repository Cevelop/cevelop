package com.cevelop.templator.instancespy.views;

import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPInstanceCache;
import org.eclipse.jface.viewers.ITreeContentProvider;


@SuppressWarnings("restriction")
public class TemplateInstanceContentProvider implements ITreeContentProvider {

    @Override
    public Object[] getElements(Object template) {
        return getChildren(template);
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ICPPInstanceCache) {
            return ((ICPPInstanceCache) parentElement).getAllInstances();
        }
        return new Object[0];
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

}
