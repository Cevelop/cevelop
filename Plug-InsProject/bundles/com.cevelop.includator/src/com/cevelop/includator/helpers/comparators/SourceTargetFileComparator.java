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

import com.cevelop.includator.dependency.DeclarationReferenceDependency;


public class SourceTargetFileComparator implements Comparator<DeclarationReferenceDependency> {

    @Override
    public int compare(DeclarationReferenceDependency first, DeclarationReferenceDependency second) {
        String firstString = getFromToPath(first);
        String secondString = getFromToPath(second);
        return firstString.compareTo(secondString);
    }

    private String getFromToPath(DeclarationReferenceDependency dependency) {
        return dependency.getDeclarationReference().getFile().getFilePath() + "->" + dependency.getDeclaration().getFile().getFilePath();
    }
}
