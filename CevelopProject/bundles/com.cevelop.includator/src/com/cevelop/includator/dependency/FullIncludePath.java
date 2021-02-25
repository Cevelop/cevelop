/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.dependency;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;


public class FullIncludePath implements IncludePath, Cloneable {

    private ArrayList<IIndexInclude> pathElements;
    private String                   toFullString;

    public FullIncludePath() {
        pathElements = new ArrayList<>();
    }

    public IIndexInclude getIncludeAt(int pos) {
        if ((pos >= 0) && (pos < pathElements.size())) {
            return pathElements.get(pos);
        }
        return null;
    }

    public void addPathElement(IIndexInclude elementToAdd) {
        toFullString = null;
        pathElements.add(elementToAdd);
    }

    @Override
    public String toString() {
        return toFullString();
    }

    private String constructToFullString() {
        if (pathElements.isEmpty()) {
            return "[empty include path]";
        }
        StringBuilder builder = new StringBuilder();
        for (IIndexInclude curElement : pathElements) {
            try {
                builder.append(FileHelper.uriToStringPath(curElement.getIncludesLocation().getURI()));
            } catch (CoreException e) {
                throw new IncludatorException(e);
            }
            builder.append(" -> ");
        }
        builder.delete(builder.length() - 4, builder.length());
        return builder.toString();
    }

    @Override
    public FullIncludePath clone() {
        try {
            FullIncludePath newPath = (FullIncludePath) super.clone();
            newPath.pathElements = new ArrayList<>(pathElements);
            return newPath;
        } catch (CloneNotSupportedException e) {
            throw new IncludatorException(e);
        }
    }

    public void removeLastElement() {
        toFullString = null;
        pathElements.remove(pathElements.size() - 1);
    }

    public Collection<IIndexInclude> getAllIncludes() {
        return pathElements;
    }

    @Override
    public IIndexInclude getFirstInclude() {
        return getIncludeAt(0);
    }

    @Override
    public IIndexInclude getLastInclude() {
        return getIncludeAt(pathElements.size() - 1);
    }

    public String toFullString() {
        if (toFullString == null) {
            toFullString = constructToFullString();
        }
        return toFullString;
    }

    @Override
    public void clear() {
        pathElements.clear();
        pathElements = null;
    }

    public int length() {
        return pathElements.size();
    }
}
