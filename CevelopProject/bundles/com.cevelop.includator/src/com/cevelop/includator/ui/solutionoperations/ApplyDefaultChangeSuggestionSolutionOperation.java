package com.cevelop.includator.ui.solutionoperations;

import java.util.Collection;

import org.eclipse.ui.PlatformUI;

import com.cevelop.includator.optimizer.IncludatorQuickFix;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.ui.ApplySuggestionsRunnable;


public class ApplyDefaultChangeSuggestionSolutionOperation extends SuggestionSolutionOperation {

    @Override
    public String getColumnDispalyName() {
        return "Apply Change";
    }

    @Override
    public String getColumnToolTipText() {
        return "This option will directly apply the default solution to your source code.";
    }

    @Override
    public void executeOn(Collection<Suggestion<?>> suggestions) {
        PlatformUI.getWorkbench().getDisplay().syncExec(new ApplySuggestionsRunnable(suggestions));
    }

    @Override
    public String getToolTipFor(Suggestion<?> suggestion) {
        IncludatorQuickFix defaultQuickFix = suggestion.getDefaultQuickFix();
        return defaultQuickFix.getLabel() + "\n\n" + defaultQuickFix.getDescription();
    }
}
