/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.findunusedfiles;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Path;

import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.IncludatorQuickFix;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.SuggestionInitializationData;
import com.cevelop.includator.optimizer.SuppressSuggestionQuickFix;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.ui.Markers;


public class UnusedFileSuggestion extends Suggestion<SuggestionInitializationData> {

    private final String description;

    public UnusedFileSuggestion(IncludatorFile file, Class<? extends Algorithm> originAlgorithm) {
        super(new SuggestionInitializationData(file), originAlgorithm);
        description = "The file '" + file.getSmartPath() + "' is unneeded.";
    }

    @Override
    protected void init() {
        // nothing to do here.
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getInitialEndOffset() {
        return 0;
    }

    @Override
    public int getSeverity() {
        return IMarker.SEVERITY_WARNING;
    }

    @Override
    public int getInitialStartOffset() {
        return 0;
    }

    @Override
    protected String getSuggestionId() {
        return "IncludatorUnusedFileSuggestionID";
    }

    @Override
    public String getMarkerType() {
        return Markers.INCLUDATOR_UNUSED_FILE_MARKER;
    }

    @Override
    public IncludatorQuickFix[] createQuickFixes() {
        return new IncludatorQuickFix[] { new RemoveFileQuickFix(this), new SuppressSuggestionQuickFix(this) };
    }

    @Override
    public String getSuppressSuggestionTargetFileName() {
        return new Path(getProjectRelativePath()).lastSegment();
    }
}
