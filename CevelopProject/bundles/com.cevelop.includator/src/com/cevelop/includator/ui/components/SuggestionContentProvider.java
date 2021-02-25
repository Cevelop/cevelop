/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.ui.components;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.ui.solutionoperations.SuggestionSolutionOperation;


public class SuggestionContentProvider extends TreeContentProvider<SuggestionTreeElement> {

    private LinkedHashMap<String, SuggestionTreeElement> fileElements;
    private SuggestionTreeElement                        root;

    public SuggestionTreeElement getTopElement() {
        return root;
    }

    private void initElements(Collection<Suggestion<?>> suggestions, SuggestionSolutionOperation defaultOperation) {
        root = new SuggestionTreeElement(null, "All");
        fileElements = new LinkedHashMap<>();
        for (Suggestion<?> curSuggestion : suggestions) {
            if (!fileElements.containsKey(curSuggestion.getAbsoluteFilePath())) {
                SuggestionTreeElement treeElement = new SuggestionTreeElement(null, "File: " + curSuggestion.getProjectRelativePath());
                fileElements.put(curSuggestion.getAbsoluteFilePath(), treeElement);
                root.addChild(treeElement);
            }
            fileElements.get(curSuggestion.getAbsoluteFilePath()).addChild(new SuggestionTreeElement(curSuggestion, null));
        }
        for (SuggestionTreeElement curElement : fileElements.values()) {
            curElement.initSuggestionOperationsFromChildren();
        }
        root.initSuggestionOperationsFromChildren();
        root.setChecked(defaultOperation, false);
    }

    @Override
    public SuggestionTreeElement[] getElements(Object inputElement) {
        if (fileElements.isEmpty()) {
            return new SuggestionTreeElement[0];
        } else if (fileElements.size() == 1) {
            return new SuggestionTreeElement[] { fileElements.values().iterator().next() };
        } else {
            return new SuggestionTreeElement[] { root };
        }
    }

    public void setSuggestion(List<Suggestion<?>> suggestions, SuggestionSolutionOperation defaultOperation) {
        initElements(suggestions, defaultOperation);
    }
}
