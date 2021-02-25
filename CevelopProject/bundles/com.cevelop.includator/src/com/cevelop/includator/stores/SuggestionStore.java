/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.stores;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.TextSelection;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.optimizer.Suggestion;


public class SuggestionStore {

    private final List<Suggestion<?>> suggestions;

    public SuggestionStore() {
        suggestions = new ArrayList<>();
    }

    public void addSuggestions(Collection<Suggestion<?>> suggestionsToAdd) {
        suggestions.removeAll(suggestionsToAdd);
        suggestions.addAll(suggestionsToAdd);
    }

    public List<Suggestion<?>> findSuggestions(String absoluteFilePath, int offset) {

        List<Suggestion<?>> result = new ArrayList<>(1);
        for (Suggestion<?> suggestion : suggestions) {
            if (suggestion.matchesPosition(absoluteFilePath, offset)) {
                result.add(suggestion);
            }
        }
        return result;
    }

    public List<Suggestion<?>> findSuggestions(URI absoluteFilePath, int offset) {
        return findSuggestions(absoluteFilePath.getPath(), offset);
    }

    public void removeSuggestion(Suggestion<?> suggestion) {
        suggestion.dispose();
        suggestions.remove(suggestion);
    }

    public void clear() {
        for (Suggestion<?> curSuggestion : suggestions) {
            curSuggestion.dispose();
        }
        suggestions.clear();
    }

    public List<Suggestion<?>> getAllSuggestions() {
        return suggestions;
    }

    public List<Suggestion<?>> findSuggestionsForLine(String absoluteFilePath, int expectedLinenr) {

        List<Suggestion<?>> result = new ArrayList<>(1);
        for (Suggestion<?> suggestion : suggestions) {
            if (suggestion.getAbsoluteFilePath().equals(absoluteFilePath)) {
                int linenr = new TextSelection(FileHelper.getDocument(FileHelper.stringToUri(absoluteFilePath)), suggestion.getStartOffset(), 0)
                        .getStartLine();
                if (expectedLinenr == linenr) {
                    result.add(suggestion);
                }
            }
        }
        return result;
    }

    public void addSuggestion(Suggestion<?> suggestionToAdd) {
        suggestions.remove(suggestionToAdd);
        suggestions.add(suggestionToAdd);
    }

    public void cleanOldSuggestions() {
        Iterator<Suggestion<?>> iter = suggestions.iterator();
        while (iter.hasNext()) {
            Suggestion<?> suggestion = iter.next();
            IMarker marker = suggestion.getMarker();
            if ((marker == null) || !marker.exists()) {
                suggestion.dispose();
                iter.remove();
            }
        }
    }

    public Suggestion<?> findSuggestion(IMarker markerToFind) {
        for (Suggestion<?> curSuggestion : suggestions) {
            IMarker curMarker = curSuggestion.getMarker();
            if (curMarker != null && curMarker.equals(markerToFind)) {
                return curSuggestion;
            }
        }
        return null;
    }

    public List<Suggestion<?>> findSuggestionsInFile(String absoluteFilePath) {
        List<Suggestion<?>> result = new ArrayList<>();
        for (Suggestion<?> curSuggestion : suggestions) {
            if (curSuggestion.getAbsoluteFilePath().equals(absoluteFilePath)) {
                result.add(curSuggestion);
            }
        }
        return result;
    }

    public Suggestion<?> removeSuggestion(IMarker marker) {
        Iterator<Suggestion<?>> it = suggestions.iterator();
        while (it.hasNext()) {
            Suggestion<?> next = it.next();
            if (marker.equals(next.getMarker())) {
                it.remove();
                return next;
            }
        }
        return null;
    }

}
