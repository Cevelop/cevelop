/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.ui;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.Optimizer;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.SuggestionOperationMapProvider;
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;
import com.cevelop.includator.stores.SuggestionStore;
import com.cevelop.includator.ui.helpers.IncludatorEclipseUIHelper;
import com.cevelop.includator.ui.solutionoperations.SuggestionSolutionOperation;


public class OptimizationRunner {

    private final Optimizer                      optimizer;
    private final IProgressMonitor               monitor;
    private final AlgorithmStartingPoint         startingPoint;
    private final Algorithm                      algorithm;
    private final SuggestionOperationMapProvider suggestionOperationMapProvider;
    private boolean                              shouldPerformCustomMapOperation;

    public OptimizationRunner(AlgorithmStartingPoint startingPoint, IProgressMonitor monitor, Algorithm algorithm,
                              SuggestionOperationMapProvider suggestionOperationMapProvider) {
        this.startingPoint = startingPoint;
        this.monitor = monitor;
        this.algorithm = algorithm;
        this.suggestionOperationMapProvider = suggestionOperationMapProvider;
        optimizer = new Optimizer(IncludatorPlugin.getWorkspace(), algorithm);
    }

    public boolean run() throws InterruptedException {

        boolean shouldRun = IncludatorEclipseUIHelper.syncSaveAllEditors();
        if (shouldRun) {
            IncludatorEclipseUIHelper.joinIndexer();
            startingPoint.getProject().acquireIndexReadLock();
            try {
                cleanupMarkers();
                optimizer.run(startingPoint, monitor);
            } finally {
                startingPoint.getProject().releaseIndexReadLock();
            }
        }
        return shouldRun;
    }

    private void cleanupMarkers() {
        try {
            IResource res = startingPoint.getAffectedResource();
            if (res == null) {
                return;
            }
            IMarker[] markers = res.findMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_INFINITE);
            for (IMarker marker : markers) {
                SuggestionStore store = IncludatorPlugin.getSuggestionStore();
                Suggestion<?> suggestion = store.findSuggestion(marker);
                if (suggestion != null && suggestion.originsFrom(algorithm)) {
                    store.removeSuggestion(suggestion);
                }
            }
        } catch (CoreException e) {
            new IncludatorException(e);
        }
    }

    private void showSuggestionDialog() {
        suggestionOperationMapProvider.performCustomOperation();
    }

    public void performUserAction() {
        performDialogActions();
        Map<SuggestionSolutionOperation, List<Suggestion<?>>> suggestionOperationMap = suggestionOperationMapProvider.getSuggestionOperationMap();
        if (suggestionOperationMap.isEmpty()) {
            return;
        }
        startingPoint.getProject().acquireIndexReadLock();
        try {
            runOperations(suggestionOperationMap);
        } finally {
            startingPoint.getProject().releaseIndexReadLock();
        }
    }

    private void runOperations(Map<SuggestionSolutionOperation, List<Suggestion<?>>> suggestionOperationMap) {
        for (Entry<SuggestionSolutionOperation, List<Suggestion<?>>> curEntry : suggestionOperationMap.entrySet()) {
            SuggestionSolutionOperation op = curEntry.getKey();
            op.executeOn(curEntry.getValue());
            if (op.shouldDisposeAfterRun()) {
                disposeUnusedSuggestions(curEntry.getValue());
            }
        }
    }

    private void disposeUnusedSuggestions(List<Suggestion<?>> unusedSuggestions) {
        for (Suggestion<?> curSuggestion : unusedSuggestions) {
            curSuggestion.dispose();
        }
    }

    private void performDialogActions() {
        SuggestionSolutionOperation defaultOperation = IncludatorPropertyManager.getDefaultDialogOperation(startingPoint.getProject());
        suggestionOperationMapProvider.setSuggestions(optimizer.getOptimizationSuggestions(), defaultOperation);
        if (shouldPerformCustomMapOperation) {
            showSuggestionDialog();
        }
    }

    public void setShouldPerformCustomMapOperation(boolean value) {
        shouldPerformCustomMapOperation = value;
    }
}
