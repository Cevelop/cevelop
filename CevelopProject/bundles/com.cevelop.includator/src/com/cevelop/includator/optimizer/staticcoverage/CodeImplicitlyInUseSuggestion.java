package com.cevelop.includator.optimizer.staticcoverage;

import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.ui.Markers;


public class CodeImplicitlyInUseSuggestion extends CoverageSuggestion {

    public CodeImplicitlyInUseSuggestion(int offset, int length, String message, IncludatorFile file, Class<? extends Algorithm> originAlgorithm) {
        super(offset, length, message, file, originAlgorithm);
    }

    @Override
    protected String getSuggestionId() {
        return "com.cevelop.includator.IncludedCodeImplicitlyInUseSuggestionID";
    }

    @Override
    public String getMarkerType() {
        return Markers.COVERAGE_IMPLICITLY_IN_USE_MARKER;
    }
}
