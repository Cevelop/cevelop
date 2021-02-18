/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.IncludatorCommentHelper;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.ProgressMonitorHelper;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;


public abstract class Algorithm {

    protected IProgressMonitor       monitor;
    private Set<Suggestion<?>>       suggestions;
    protected AlgorithmStartingPoint startingPoint;
    private Map<String, Set<String>> suppressedSuggestionList;

    public void start(AlgorithmStartingPoint startingPoint, IProgressMonitor monitor) {
        this.startingPoint = startingPoint;
        this.monitor = monitor;
        try {
            String initialMesg = getInitialProgressMonitorMessage(startingPoint.getAffectedResourceName());
            monitor.setTaskName(initialMesg);
            monitor.beginTask(initialMesg, ProgressMonitorHelper.ALG_WORK);
            initSuggestionList();
            run();
            postRun();
            IncludatorCommentHelper.extendLocationsWithSurroundingCommentsLocation(suggestions);
            initCreatedSuggestions();
        } finally {
            monitor.done();
        }
    }

    private void initCreatedSuggestions() {
        for (Suggestion<?> curSuggestion : suggestions) {
            curSuggestion.initIfRequired();
        }
    }

    protected void initSuggestionList() {
        suggestions = new LinkedHashSet<>();
    }

    protected abstract void run();

    protected void postRun() {
        // do nothing. Subclasses can override if desired.
    };

    public abstract AlgorithmScope getScope();

    public abstract Set<Class<? extends Algorithm>> getInvolvedAlgorithmTypes();

    public final Collection<Suggestion<?>> getSuggestions() {
        return suggestions;
    }

    public abstract String getInitialProgressMonitorMessage(String resourceName);

    protected void monitorWorked(double percentage) {
        monitor.worked((int) (ProgressMonitorHelper.ALG_WORK * percentage));
    }

    protected void addSuggestion(Suggestion<?> suggestion) {
        if (!shouldSuppressSuggestion(suggestion)) {
            suggestions.add(suggestion);
        } else {
            String msg = "Prevented adding of suggestion '" + suggestion.getDescription() +
                         "' caused by entry in Suppress-Suggestion list (see project properties).";
            IncludatorPlugin.logStatus(new IncludatorStatus(IStatus.INFO, msg), suggestion.getAbsoluteFilePath());
        }
    }

    private boolean shouldSuppressSuggestion(Suggestion<?> suggestion) {
        String suppressSuggestionTargetFileName = suggestion.getSuppressSuggestionTargetFileName();
        if (suppressSuggestionTargetFileName == null) {
            return false;
        }
        Set<String> suppressedTargetFiles = suppressedSuggestionList.get(suggestion.getProjectRelativePath());
        if (suppressedTargetFiles == null) {
            return false;
        }
        return suppressedTargetFiles.contains(suppressSuggestionTargetFileName);
    }

    protected void removeSuggestion(Suggestion<?> suggestion) {
        suggestions.remove(suggestion);
    }

    protected void addAllSuggestions(Collection<Suggestion<?>> suggestionsToAdd) {
        suggestions.addAll(suggestionsToAdd);
    }

    protected void setProgressMonitorMessage(String newMessage) {
        monitor.setTaskName(newMessage);
    }

    public void reset() {
        monitor = null;
        startingPoint = null;
        suggestions = null;
    }

    public void setSuppressionList(Map<String, Set<String>> suppressedSuggestionList) {
        this.suppressedSuggestionList = suppressedSuggestionList;
    }
}
