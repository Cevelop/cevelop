/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.NodeCommentMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.MarkerHelper;
import com.cevelop.includator.ui.Markers;
import com.cevelop.includator.ui.helpers.PositionTrackingChange;
import com.cevelop.includator.ui.solutionoperations.AddMarkerSuggestionSolutionOperation;
import com.cevelop.includator.ui.solutionoperations.ApplyDefaultChangeSuggestionSolutionOperation;
import com.cevelop.includator.ui.solutionoperations.NothingSuggestionSolutionOperation;
import com.cevelop.includator.ui.solutionoperations.SuggestionSolutionOperation;
import com.cevelop.includator.ui.solutionoperations.SuppressInFutureSuggestionSolutionOperation;


@SuppressWarnings("restriction")
public abstract class Suggestion<InitializationDataType extends SuggestionInitializationData> {

    private IMarker marker;

    private IncludatorQuickFix[]             quickFixes;
    private final String                     projectRelativePath;
    private final Class<? extends Algorithm> originAlgorithm;
    private final String                     absoluteFilePath;
    private final IFile                      iFile;

    public static final SuggestionSolutionOperation       SOLUTION_OPERATION_ADD_MARKER           = new AddMarkerSuggestionSolutionOperation();
    public static final SuggestionSolutionOperation       SOLUTION_OPERATION_APPLY_DEFAULT_CHANGE =
                                                                                                  new ApplyDefaultChangeSuggestionSolutionOperation();
    public static final SuggestionSolutionOperation       SOLUTION_OPERATION_OMIT_IN_FUTURE       = new SuppressInFutureSuggestionSolutionOperation();
    public static final SuggestionSolutionOperation       SOLUTION_OPERATION_DO_NOTHING           = new NothingSuggestionSolutionOperation();
    public static final List<SuggestionSolutionOperation> DEFAULT_SUGGESTION_SOLUTION_OPERATIONS;

    public final InitializationDataType initData;

    private boolean wasInitialized;

    static {
        DEFAULT_SUGGESTION_SOLUTION_OPERATIONS = new ArrayList<>();
        DEFAULT_SUGGESTION_SOLUTION_OPERATIONS.add(SOLUTION_OPERATION_ADD_MARKER);
        DEFAULT_SUGGESTION_SOLUTION_OPERATIONS.add(SOLUTION_OPERATION_APPLY_DEFAULT_CHANGE);
        DEFAULT_SUGGESTION_SOLUTION_OPERATIONS.add(SOLUTION_OPERATION_OMIT_IN_FUTURE);
        DEFAULT_SUGGESTION_SOLUTION_OPERATIONS.add(SOLUTION_OPERATION_DO_NOTHING);
    }

    public abstract String getDescription();

    public abstract int getSeverity();

    protected abstract int getInitialEndOffset();

    protected abstract int getInitialStartOffset();

    protected abstract String getSuggestionId();

    protected abstract IncludatorQuickFix[] createQuickFixes();

    /**
     * Any work intensive suggestion initialization (like creating changes, ast-rewrites, calculating insertion offsets) should be done here instead
     * of in the constructor because init will only be called, if the suggestion might actually be applied. It will not be applied if omitted by
     * {@code Suggestion<?>} Suppression list.
     */
    protected abstract void init();

    /**
     * No work intensive task should be executed here. The constructor must make sure that {@link #getSuppressSuggestionTargetFileName()} already
     * returns its
     * meant value.
     *
     * @param initData
     * The initial data as {@link InitializationDataType}
     * @param originAlgorithm
     * The class of the original {@link Algorithm}
     */
    public Suggestion(InitializationDataType initData, Class<? extends Algorithm> originAlgorithm) {
        this.initData = initData;
        projectRelativePath = initData.file.getProjectRelativePath();
        absoluteFilePath = initData.file.getFilePath();
        iFile = initData.file.getIFile();
        this.originAlgorithm = originAlgorithm;
    }

    public void initIfRequired() {
        if (!wasInitialized) {
            wasInitialized = true;
            init();
            if (initData.commentMap != null) {
                extendLocationsWithSurroundingCommentsLocation(initData.commentMap);
            }
            initData.dispose();
        }
    }

    public int getStartOffset() {
        try {
            IMarker activeMarker = getMarker();
            if ((activeMarker != null) && activeMarker.exists()) {
                return MarkerHelper.getMarkerStartOffset(activeMarker);
            } else {
                return getInitialStartOffset();
            }
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
    }

    public int getEndOffset() {
        try {
            IMarker activeMarker = getMarker();
            if ((activeMarker != null) && activeMarker.exists()) {
                return MarkerHelper.getMarkerEndOffset(activeMarker);
            } else {
                return getInitialEndOffset();
            }
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
    }

    public int getLength() {
        return getEndOffset() - getStartOffset();
    }

    public String getMarkerType() {
        return Markers.INCLUDATOR_PPROBLEM_MARKER;
    }

    public String getAbsoluteFilePath() {
        return absoluteFilePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Suggestion)) {
            return false;
        }
        Suggestion<?> other = (Suggestion<?>) obj;
      //@formatter:off
		return getSuggestionId().equals(other.getSuggestionId())
		&& getAbsoluteFilePath().equals(other.getAbsoluteFilePath())
		&& (getStartOffset() == other.getStartOffset())
		&& (getEndOffset() == other.getEndOffset())
		&& (getDescription().equals(other.getDescription()));
		//@formatter:on
    }

    @Override
    public int hashCode() {
        return getAbsoluteFilePath().hashCode() + 59 * getDescription().hashCode();
    }

    public boolean matchesPosition(String absoluteFilePath, int offset) {
        boolean fileMatches = getAbsoluteFilePath().equals(absoluteFilePath);
        return fileMatches && (getStartOffset() <= offset) && (getEndOffset() >= offset);
    }

    public boolean hasQuickFix() {
        return getQuickFixes() != null;
    }

    public void setMarkerProperties(IMarker marker) throws CoreException {
        marker.setAttribute(IMarker.MESSAGE, getDescription());
        marker.setAttribute(IMarker.SEVERITY, getSeverity());
        marker.setAttribute(IMarker.CHAR_START, getStartOffset());
        marker.setAttribute(IMarker.CHAR_END, getEndOffset());
        this.marker = marker;
    }

    public void removeMarker() {
        try {
            if (marker != null && marker.getResource().isAccessible()) {
                marker.delete();
                marker = null;
            }
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
    }

    public IncludatorQuickFix getDefaultQuickFix() {
        initQuickFixes();
        return quickFixes.length > 0 ? quickFixes[0] : null;
    }

    public IncludatorQuickFix getSuppressQuickFix() {
        initQuickFixes();
        return quickFixes.length > 1 && quickFixes[1] instanceof SuppressSuggestionQuickFix ? quickFixes[1] : null;
    }

    public IncludatorQuickFix[] getQuickFixes() {
        initQuickFixes();
        return quickFixes;
    }

    private void initQuickFixes() {
        if (quickFixes == null) {
            if (canCreateQuickFix()) {
                quickFixes = createQuickFixes();
            } else {
                quickFixes = new IncludatorQuickFix[0];
            }
        }
    }

    private boolean canCreateQuickFix() {
        return getIFile() != null;
    }

    public boolean originsFrom(Algorithm algorithm) {
        for (Class<? extends Algorithm> subAlgorithm : algorithm.getInvolvedAlgorithmTypes()) {
            if (originAlgorithm.isAssignableFrom(subAlgorithm)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    public IFile getIFile() {
        return iFile;
    }

    public String getProjectRelativePath() {
        return projectRelativePath;
    }

    public IMarker getMarker() {
        return marker;
    }

    public void dispose() {
        removeMarker();
    }

    public PositionTrackingChange getContainedPositionTrackingChange() {
        // normal Suggestion<?> do not have a PositionTracking-change by default. Special suggestions which do have one (because e.g. an additional
        // insert position was calculated), child classes can override here.
        return null;
    }

    /**
     * TODO: use this (if returning true and suggestion length == 0) to delete current marker. (Ticket http://www.includator.com/issues/46)
     *
     * @return {@code false} per default
     */
    public boolean shouldAutoDeleteWhenChangeSizeEmpty() {
        return false;
    }

    /**
     * Subclasses can override this to extend their suggestion location. IMORTANT after returning from this method, Suggestions are not allowed to
     * keep references to ASTNodes, IncludatorFiles/Projects since these will get released.
     *
     * @param commentMap
     * A {@link NodeCommentMap} containing the comments, here unused.
     */
    public void extendLocationsWithSurroundingCommentsLocation(NodeCommentMap commentMap) {}

    public List<SuggestionSolutionOperation> getSolutionOperations() {
        return DEFAULT_SUGGESTION_SOLUTION_OPERATIONS;
    }

    public abstract String getSuppressSuggestionTargetFileName();
}
