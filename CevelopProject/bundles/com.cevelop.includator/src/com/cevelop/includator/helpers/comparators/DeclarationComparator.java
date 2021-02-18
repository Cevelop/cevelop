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

import com.cevelop.includator.cxxelement.Declaration;


public class DeclarationComparator implements Comparator<Declaration> {

    @Override
    public int compare(Declaration first, Declaration second) {
        int offsetCompareResult = first.getFileLocation().getNodeOffset() - second.getFileLocation().getNodeOffset();
        if (offsetCompareResult != 0) {
            return offsetCompareResult;
        }
        int signatureCompareResult = first.getName().toString().compareTo(second.getName().toString());
        if (signatureCompareResult != 0) {
            return signatureCompareResult;
        }
        return first.getFileLocation().getFileName().compareTo(second.getFileLocation().getFileName());
    }
}
