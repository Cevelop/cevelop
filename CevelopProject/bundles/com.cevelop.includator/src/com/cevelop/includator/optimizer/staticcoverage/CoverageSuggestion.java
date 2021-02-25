/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.staticcoverage;

import org.eclipse.core.resources.IMarker;

import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.IncludatorQuickFix;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.SuggestionInitializationData;
import com.cevelop.includator.resources.IncludatorFile;


public abstract class CoverageSuggestion extends Suggestion<SuggestionInitializationData> {

    private final int    offset;
    private final int    length;
    private final String message;

    public CoverageSuggestion(int offset, int length, String message, IncludatorFile file, Class<? extends Algorithm> originAlgorithm) {
        super(new SuggestionInitializationData(file), originAlgorithm);
        this.message = message;
        this.offset = offset;
        this.length = length;
    }

    @Override
    protected void init() {
        // nothing to do here.
    }

    @Override
    public abstract String getMarkerType();

    @Override
    public final String getDescription() {
        return message;
    }

    @Override
    public final int getSeverity() {
        return IMarker.SEVERITY_INFO;
    }

    @Override
    protected final int getInitialEndOffset() {
        return offset + length;
    }

    @Override
    protected final int getInitialStartOffset() {
        return offset;
    }

    @Override
    public final IncludatorQuickFix[] createQuickFixes() {
        return new IncludatorQuickFix[0];
    }

    @Override
    public String getSuppressSuggestionTargetFileName() {
        return null;
    }
}
