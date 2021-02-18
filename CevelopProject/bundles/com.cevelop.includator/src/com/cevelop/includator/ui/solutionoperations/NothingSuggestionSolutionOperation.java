package com.cevelop.includator.ui.solutionoperations;

import java.util.Collection;

import com.cevelop.includator.optimizer.Suggestion;


public class NothingSuggestionSolutionOperation extends SuggestionSolutionOperation {

    @Override
    public void executeOn(Collection<Suggestion<?>> suggestions) {
        // Do nothing
    }

    @Override
    public String getColumnDispalyName() {
        return "Nothing";
    }

    @Override
    public String getColumnToolTipText() {
        return "Choosing this option will make Includator ignore a suggestion when clicking on \"ok\".";
    }

    @Override
    public String getToolTipFor(Suggestion<?> suggestion) {
        return getColumnToolTipText();
    }
}
