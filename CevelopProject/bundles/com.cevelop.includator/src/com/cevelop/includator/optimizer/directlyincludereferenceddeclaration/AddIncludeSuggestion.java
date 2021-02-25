/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.directlyincludereferenceddeclaration;

import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.NodeCommentMap;
import org.eclipse.core.resources.IMarker;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludeInfo;
import com.cevelop.includator.helpers.offsetprovider.InsertIncludeOffsetProvider;
import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.IncludatorQuickFix;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.SuggestionInitializationData;
import com.cevelop.includator.optimizer.SuppressSuggestionQuickFix;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.ui.Markers;


@SuppressWarnings("restriction")
public class AddIncludeSuggestion extends Suggestion<SuggestionInitializationData> {

    private final IncludeInfo           include;
    private InsertIncludeOffsetProvider offsetProvider;

    public AddIncludeSuggestion(IncludeInfo includeToAdd, IncludatorFile file, Class<? extends Algorithm> originAlgorithm) {
        super(new SuggestionInitializationData(file), originAlgorithm);
        this.include = includeToAdd;
    }

    @Override
    protected void init() {
        offsetProvider = new InsertIncludeOffsetProvider(initData.getFile().getTranslationUnit(), include.isSystemInclude());
    }

    @Override
    public String getDescription() {
        return "Missing '" + include.getIncludeStatementString() + "'.";
    }

    @Override
    public int getSeverity() {
        return IMarker.SEVERITY_WARNING;
    }

    @Override
    protected int getInitialEndOffset() {
        return getInitialStartOffset() + (offsetProvider.isOffsetOnNewlineChar() ? FileHelper.NL_LENGTH : 0);
    }

    @Override
    protected int getInitialStartOffset() {
        return offsetProvider.getInsertOffset();
    }

    @Override
    protected String getSuggestionId() {
        return "com.cevelop.includator.directlyincludereferenceddeclarationsID";
    }

    @Override
    public String getMarkerType() {
        return Markers.INCLUDATOR_ADD_INCLUDE_MARKER;
    }

    @Override
    public IncludatorQuickFix[] createQuickFixes() {
        return new IncludatorQuickFix[] { new AddIncludeQuickFix(this), new SuppressSuggestionQuickFix(this) };
    }

    public IncludeInfo getIncludeToAdd() {
        return include;
    }

    @Override
    public String getSuppressSuggestionTargetFileName() {
        String includeDirective = include.getIncludeStatementString();
        return includeDirective.substring(10, includeDirective.length() - 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AddIncludeSuggestion)) {
            return false;
        }
        AddIncludeSuggestion other = (AddIncludeSuggestion) obj;
        return getAbsoluteFilePath().equals(other.getAbsoluteFilePath()) && (getDescription().equals(other.getDescription()));
    }

    public InsertIncludeOffsetProvider getOffsetProvider() {
        return offsetProvider;
    }

    @Override
    public void extendLocationsWithSurroundingCommentsLocation(NodeCommentMap commentMap) {
        offsetProvider.adaptOffsetWithCommentMap(commentMap, initData.getFile().getTranslationUnit());
    }
}
