/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers.comparators;

import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.dependency.FirstLastElementIncludePath;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;


public class IndexFirstLastElementIncludePathComparator implements Comparator<FirstLastElementIncludePath> {

    @Override
    public int compare(FirstLastElementIncludePath first, FirstLastElementIncludePath second) {

        try {
            String firstPathFirstElementPath = FileHelper.uriToStringPath(first.getFirstInclude().getIncludesLocation().getURI());
            String secondPathFirstElementPath = FileHelper.uriToStringPath(second.getFirstInclude().getIncludesLocation().getURI());
            int firstElementCompare = firstPathFirstElementPath.compareTo(secondPathFirstElementPath);
            if (firstElementCompare != 0) {
                return firstElementCompare;
            }
            String firstPathSecondElementPath = FileHelper.uriToStringPath(first.getLastInclude().getIncludesLocation().getURI());
            String secondPathSecondElementPath = FileHelper.uriToStringPath(second.getLastInclude().getIncludesLocation().getURI());
            return firstPathSecondElementPath.compareTo(secondPathSecondElementPath);
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
    }

}
