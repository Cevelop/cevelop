package com.cevelop.includator.ui;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.PlatformUI;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.optimizer.IncludatorQuickFix;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.stores.SuggestionStore;


public class IncludatorResolutionGenerator implements IMarkerResolutionGenerator {

    private static final IncludatorQuickFix[] EMPTY_ARRAY = new IncludatorQuickFix[0];

    @Override
    public IMarkerResolution[] getResolutions(IMarker marker) {
        boolean wasWorkbenchAlreadySet = true;
        try {
            wasWorkbenchAlreadySet = IncludatorPlugin.initActiveWorkbenchWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
            Suggestion<?> suggestion = IncludatorPlugin.getSuggestionStore().findSuggestion(marker);
            return suggestion != null && suggestion.hasQuickFix() ? suggestion.getQuickFixes() : EMPTY_ARRAY;
        } finally {
            if (!wasWorkbenchAlreadySet) {
                IncludatorPlugin.resetActiveWorkbenchWindow();
            }
            FileHelper.clean();
        }
    }

    public List<IncludatorQuickFix> findQuickFixOnOffset(URI markerUri, int offset) {
        IDocument doc = FileHelper.getDocument(markerUri);
        int linenr = new TextSelection(doc, offset, 0).getStartLine();
        return findQuickFixOnLine(markerUri, linenr);
    }

    public List<IncludatorQuickFix> findQuickFixOnLine(URI markerUri, int linenr) {
        String markerPath = FileHelper.uriToStringPath(markerUri);
        ArrayList<IncludatorQuickFix> proposalList = new ArrayList<>();
        SuggestionStore store = IncludatorPlugin.getSuggestionStore();
        List<Suggestion<?>> suggestions = store.findSuggestionsForLine(markerPath, linenr);
        for (Suggestion<?> suggestion : suggestions) {
            if (suggestion.hasQuickFix()) {
                proposalList.addAll(Arrays.asList(suggestion.getQuickFixes()));
            }
        }
        return proposalList;
    }

}
