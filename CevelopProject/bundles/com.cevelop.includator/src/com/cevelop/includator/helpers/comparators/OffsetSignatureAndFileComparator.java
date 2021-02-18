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

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.helpers.FileHelper;


public class OffsetSignatureAndFileComparator implements Comparator<DeclarationReference> {

    @Override
    public int compare(DeclarationReference first, DeclarationReference second) {
        int firstOffset = FileHelper.getNodeFileLocation(first.getASTNode()).getNodeOffset();
        int secondOffset = FileHelper.getNodeFileLocation(second.getASTNode()).getNodeOffset();
        if (firstOffset == secondOffset) {
            return first.getASTNode().toString().compareTo(second.getASTNode().toString());
        } else {
            return firstOffset - secondOffset;
        }
    }
}
