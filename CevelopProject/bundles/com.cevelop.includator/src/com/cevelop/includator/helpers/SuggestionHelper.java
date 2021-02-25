package com.cevelop.includator.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.Suggestion;


public class SuggestionHelper {

    public static Map<IFile, Collection<Suggestion<?>>> groupSuggestionsPerFile(Collection<Suggestion<?>> suggestions) {
        LinkedHashMap<IFile, Collection<Suggestion<?>>> suggestionsPerFileMap = new LinkedHashMap<>();
        for (Suggestion<?> curSuggestion : suggestions) {
            IFile file = curSuggestion.getIFile();
            if (file == null) {
                String filePath = FileHelper.getSmartFilePath(curSuggestion.getAbsoluteFilePath(), curSuggestion.getProjectRelativePath());
                String msg = "Suggestion '" + curSuggestion.getDescription() +
                             "' isn't part of an Eclipse project. Adding markers and applying suggestion-solutions is therefore not supported.";
                IncludatorPlugin.logStatus(new IncludatorStatus(msg), filePath);
                continue;
            }
            if (!suggestionsPerFileMap.containsKey(file)) {
                suggestionsPerFileMap.put(file, new ArrayList<Suggestion<?>>());
            }
            suggestionsPerFileMap.get(file).add(curSuggestion);
        }
        return suggestionsPerFileMap;
    }

    public static void initSuggestionPerFileMap(Map<IFile, Collection<Suggestion<?>>> suggestionsPerFileMap, Collection<Suggestion<?>> suggestions) {
        IncludatorCommentHelper.initCommentMap(suggestionsPerFileMap);
        for (Suggestion<?> curSuggestion : suggestions) {
            curSuggestion.initIfRequired();
        }
    }

    public static void initSuggestions(Collection<Suggestion<?>> suggestions) {
        initSuggestionPerFileMap(groupSuggestionsPerFile(suggestions), suggestions);
    }
}
