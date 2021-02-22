package com.cevelop.includator.optimizer;

import java.util.List;
import java.util.Map;

import com.cevelop.includator.ui.solutionoperations.SuggestionSolutionOperation;


public interface SuggestionOperationMapProvider {

    public Map<SuggestionSolutionOperation, List<Suggestion<?>>> getSuggestionOperationMap();

    public void performCustomOperation();

    public void setSuggestions(List<Suggestion<?>> optimizationSuggestions, SuggestionSolutionOperation defaultOperation);
}
