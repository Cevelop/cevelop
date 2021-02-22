/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.ui.actions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.SuggestionOperationMapProvider;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.resources.IncludatorProject;
import com.cevelop.includator.ui.solutionoperations.SuggestionSolutionOperation;


public class DirectlyOrganizeIncludesAction extends IncludatorAlgorithmAction implements SuggestionOperationMapProvider {

    private Map<SuggestionSolutionOperation, List<Suggestion<?>>> operationsMap;
    private List<Suggestion<?>>                                   suggestions;

    @Override
    protected Algorithm getAlgorithmToRun() {
        return new OrganizeIncludesAlgorithm();
    }

    @Override
    protected boolean shouldShowDialog(IncludatorProject project) {
        return true;
    }

    @Override
    protected SuggestionOperationMapProvider getSuggestionOperationMapProvider() {
        return this;
    }

    @Override
    public Map<SuggestionSolutionOperation, List<Suggestion<?>>> getSuggestionOperationMap() {
        return operationsMap;
    }

    @Override
    public void performCustomOperation() {
        operationsMap = new LinkedHashMap<>(1);
        operationsMap.put(Suggestion.SOLUTION_OPERATION_APPLY_DEFAULT_CHANGE, suggestions);
    }

    @Override
    public void setSuggestions(List<Suggestion<?>> suggestions, SuggestionSolutionOperation defaultOperation) {
        this.suggestions = suggestions;
    }
}
