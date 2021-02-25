/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.findunusedincludes;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.NodeCommentMap;
import org.eclipse.core.resources.IMarker;

import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.IncludatorQuickFix;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.SuppressSuggestionQuickFix;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.ui.Markers;


@SuppressWarnings("restriction")
public class UnusedIncludeSuggestion extends Suggestion<UnusedIncludeInitializationData> {

    private final String description;
    private int          initialStartOffset;
    private int          initialEndOffset;
    private final String reason;
    private final String includeName;

    public UnusedIncludeSuggestion(IASTPreprocessorIncludeStatement include, IncludatorFile includingFile, Class<? extends Algorithm> originAlgorithm,
                                   String reason) {
        super(new UnusedIncludeInitializationData(includingFile, include), originAlgorithm);
        this.reason = reason;
        String includeSignature = include.getRawSignature();
        description = "The include statement '" + includeSignature + "' is unneeded.";
        initOffsets(include);
        includeName = include.getName().toString();
    }

    @Override
    protected void init() {
        // nothing to do here.
    }

    private void initOffsets(IASTPreprocessorIncludeStatement include) {
        IASTFileLocation includeLocation = include.getFileLocation();
        initialStartOffset = includeLocation.getNodeOffset();
        initialEndOffset = initialStartOffset + includeLocation.getNodeLength();
    }

    private void initExtendedStartOffset(List<IASTComment> associatedComments) {
        for (IASTComment curComment : associatedComments) {
            int commentStartOffset = curComment.getFileLocation().getNodeOffset();
            if (commentStartOffset < initialStartOffset) {
                initialStartOffset = commentStartOffset;
            }
        }
    }

    private void initExtendedEndOffset(List<IASTComment> associatedComments) {
        for (IASTComment curComment : associatedComments) {
            int commentEndOffset = curComment.getFileLocation().getNodeOffset() + curComment.getFileLocation().getNodeLength();
            if (commentEndOffset > initialEndOffset) {
                initialEndOffset = commentEndOffset;
            }
        }
    }

    @Override
    public String getDescription() {
        return description + ((reason != null) ? " " + reason : "");
    }

    @Override
    public int getInitialEndOffset() {
        return initialEndOffset;
    }

    @Override
    public int getSeverity() {
        return IMarker.SEVERITY_WARNING;
    }

    @Override
    public int getInitialStartOffset() {
        return initialStartOffset;
    }

    @Override
    protected String getSuggestionId() {
        return "IncludatorUnusedIncludeSuggestionID";
    }

    @Override
    public String getMarkerType() {
        return Markers.INCLUDATOR_UNUSED_INCLUDE_MARKER;
    }

    @Override
    public IncludatorQuickFix[] createQuickFixes() {
        return new IncludatorQuickFix[] { new RemoveIncludeQuickFix(this), new SuppressSuggestionQuickFix(this) };
    }

    @Override
    public boolean shouldAutoDeleteWhenChangeSizeEmpty() {
        return true;
    }

    public String getIncludeName() {
        return includeName;
    }

    @Override
    public void extendLocationsWithSurroundingCommentsLocation(NodeCommentMap commentMap) {
        List<IASTComment> associatedComments = commentMap.getAllCommentsForNode(initData.include);
        initExtendedStartOffset(associatedComments);
        initExtendedEndOffset(associatedComments);
    }

    @Override
    public String getSuppressSuggestionTargetFileName() {
        return includeName;
    }
}
