/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.dependency;

import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;


public class FirstLastElementIncludePath implements IncludePath {

    private IIndexInclude firstElement;
    private IIndexInclude lastElement;

    public FirstLastElementIncludePath(IIndexInclude firstElement, IIndexInclude lastElement) {
        this.firstElement = firstElement;
        this.lastElement = lastElement;
    }

    @Override
    public String toString() {
        try {
            if ((firstElement == null) || (lastElement == null)) {
                return "[empty include path]";
            } else if (firstElement.equals(lastElement)) {
                return FileHelper.uriToStringPath(firstElement.getIncludesLocation().getURI());
            }
            return firstElement.getIncludesLocation().getURI().getPath() + " -> ... -> " + lastElement.getIncludesLocation().getURI().getPath();
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
    }

    @Override
    public IIndexInclude getFirstInclude() {
        return firstElement;
    }

    @Override
    public IIndexInclude getLastInclude() {
        return lastElement;
    }

    @Override
    public void clear() {
        firstElement = null;
        lastElement = null;
    }
}
