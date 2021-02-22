package com.cevelop.includator.tests.mocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.SuggestionOperationMapProvider;
import com.cevelop.includator.resources.IncludatorProject;
import com.cevelop.includator.ui.solutionoperations.SuggestionSolutionOperation;


public class CustomSuggestionSelectedAction extends IncludatorTestAction {

    private final LinkedHashMap<SuggestionSolutionOperation, List<String>> expectedStrMap;

    public CustomSuggestionSelectedAction(Algorithm alg) {
        super(alg);
        expectedStrMap = new LinkedHashMap<>();
    }

    public void addExpectedSelection(SuggestionSolutionOperation selectedOperation, String... suggestionsToSelectList) {
        expectedStrMap.put(selectedOperation, Arrays.asList(suggestionsToSelectList));
    }

    /*
     * Since also the UserActionProvider is replaced bellow, true is returned here to make the optimizationRunner call the evaluateUserActions. So no
     * dialog is shown anyway.
     */
    @Override
    protected boolean shouldShowDialog(IncludatorProject project) {
        return true;
    }

    @Override
    protected SuggestionOperationMapProvider getSuggestionOperationMapProvider() {
        return new SuggestionOperationMapProvider() {

            private Map<SuggestionSolutionOperation, List<Suggestion<?>>> selectedMap;
            private List<Suggestion<?>>                                   suggestions;

            @Override
            public Map<SuggestionSolutionOperation, List<Suggestion<?>>> getSuggestionOperationMap() {
                return selectedMap;
            }

            @Override
            public void performCustomOperation() {
                selectedMap = SuggestionSolutionOperation.makeEmptyMap();
                for (SuggestionSolutionOperation curOp : expectedStrMap.keySet()) {
                    selectedMap.put(curOp, new ArrayList<Suggestion<?>>());
                }
                for (Entry<SuggestionSolutionOperation, List<String>> curExpectedEntry : expectedStrMap.entrySet()) {
                    for (Suggestion<?> curSuggestion : suggestions) {
                        if (curExpectedEntry.getValue().contains(curSuggestion.getDescription())) {
                            selectedMap.get(curExpectedEntry.getKey()).add(curSuggestion);
                        }
                    }
                }
                ArrayList<Suggestion<?>> remainingSuggestions = new ArrayList<>(suggestions);
                for (List<Suggestion<?>> curSelectedSuggestions : selectedMap.values()) {
                    remainingSuggestions.removeAll(curSelectedSuggestions);
                }
                if (!remainingSuggestions.isEmpty()) {
                    throw new IncludatorException("CustomSuggestionSelectedAction did not contain any selected operation for the suggestions: " +
                                                  remainingSuggestions.toString());
                }
            }

            @Override
            public void setSuggestions(List<Suggestion<?>> suggestions, SuggestionSolutionOperation defaultOperation) {
                this.suggestions = suggestions;
            }
        };
    }
}
