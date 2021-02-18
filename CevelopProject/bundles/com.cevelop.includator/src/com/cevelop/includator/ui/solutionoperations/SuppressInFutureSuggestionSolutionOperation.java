package com.cevelop.includator.ui.solutionoperations;

import java.util.Collection;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.optimizer.Suggestion;


public class SuppressInFutureSuggestionSolutionOperation extends SuggestionSolutionOperation {

    @Override
    public String getColumnDispalyName() {
        return "Not Again";
    }

    @Override
    public String getColumnToolTipText() {
        return "This operation will make Includator not propose this suggestion again in the future.";
    }

    @Override
    public void executeOn(Collection<Suggestion<?>> suggestions) {
        for (Suggestion<?> curSuggestion : suggestions) {
            curSuggestion.getSuppressQuickFix().run(null);
        }
    }

    @Override
    public String getToolTipFor(Suggestion<?> suggestion) {
        String filePath = FileHelper.getSmartFilePath(suggestion.getAbsoluteFilePath(), suggestion.getProjectRelativePath());
        return "This will add an entry to Includator's Suggestion-Exclusion list of the current project. This will make Includator never again to make suggestions that concern the target file '" +
               suggestion.getSuppressSuggestionTargetFileName() + "' in the file '" + filePath + "'.";
    }
}
