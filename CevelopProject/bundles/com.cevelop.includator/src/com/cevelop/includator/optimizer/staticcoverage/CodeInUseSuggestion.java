/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.staticcoverage;

import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.ui.Markers;


public class CodeInUseSuggestion extends CoverageSuggestion {

    public CodeInUseSuggestion(int offset, int length, String message, IncludatorFile file, Class<? extends Algorithm> originAlgorithm) {
        super(offset, length, message, file, originAlgorithm);
    }

    @Override
    protected String getSuggestionId() {
        return "com.cevelop.includator.IncludedCodeInUseSuggestionID";
    }

    @Override
    public String getMarkerType() {
        return Markers.COVERAGE_IN_USE_MARKER;
    }
}
