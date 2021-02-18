package com.cevelop.includator.ui.solutionoperations;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.cevelop.includator.optimizer.Suggestion;


public abstract class SuggestionSolutionOperation {

    public static final int PRIORITY_ADD_MARKER   = 1;
    public static final int PRIORITY_APPLY_CHANGE = 2;
    public static final int PRIORITY_DEFAULT      = 3;

    /**
     * Note that all suggestions which are not added to the SuggestionStore by the executeOn impl, should be disposed here.
     * 
     * @param suggestions A {@link Collection} of {@link Suggestion}s.
     */
    public abstract void executeOn(Collection<Suggestion<?>> suggestions);

    public abstract String getColumnDispalyName();

    public abstract String getColumnToolTipText();

    public abstract String getToolTipFor(Suggestion<?> suggestion);

    @Override
    public String toString() {
        return "Suggestion operation " + getColumnDispalyName();
    }

    public int getPriority() {
        return PRIORITY_DEFAULT;
    }

    public boolean shouldDisposeAfterRun() {
        return true;
    }

    public static Map<SuggestionSolutionOperation, List<Suggestion<?>>> makeEmptyMap() {
        return new TreeMap<>((o1, o2) -> {
            if (o1.getPriority() != o2.getPriority()) {
                return o1.getPriority() - o2.getPriority();
            } else {
                return o1.getClass().getName().compareTo(o2.getClass().getName());
            }
        });
    }
}
