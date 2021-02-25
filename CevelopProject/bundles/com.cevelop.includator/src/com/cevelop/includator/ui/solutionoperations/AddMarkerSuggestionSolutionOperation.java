package com.cevelop.includator.ui.solutionoperations;

import java.util.Collection;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.MarkerHelper;
import com.cevelop.includator.helpers.SuggestionHelper;
import com.cevelop.includator.optimizer.Suggestion;


public class AddMarkerSuggestionSolutionOperation extends SuggestionSolutionOperation {

    @Override
    public String getColumnDispalyName() {
        return "Add Marker";
    }

    @Override
    public String getColumnToolTipText() {
        return "Choosing this option will add a marker into the C/C++ editor. You can then decide by using quick-fixes how to proceed with the suggestion.";
    }

    @Override
    public void executeOn(Collection<Suggestion<?>> suggestions) {
        SuggestionHelper.initSuggestions(suggestions);
        try {
            MarkerHelper.addMarkers(suggestions);
            IncludatorPlugin.getSuggestionStore().addSuggestions(suggestions);
        } catch (Exception e) {
            IncludatorPlugin.logStatus(new IncludatorStatus("Error while adding Includator suggestion markers.", e), (String) null);
        }
    }

    @Override
    public boolean shouldDisposeAfterRun() {
        return false;
    }

    @Override
    public String getToolTipFor(Suggestion<?> suggestion) {
        return "Adds a marker to the editor which allows to later use any of the possible solutions for this suggestion.";
    }
}
