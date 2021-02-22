package com.cevelop.includator.ui;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.cdt.internal.ui.refactoring.changes.DeleteFileChange;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.SuggestionHelper;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.ui.helpers.ChangeHelper;


@SuppressWarnings("restriction")
public class ApplySuggestionsRunnable implements Runnable {

    private final Map<IFile, Collection<Suggestion<?>>> suggestionsPerFileMap;

    public ApplySuggestionsRunnable(Collection<Suggestion<?>> suggestions) {
        suggestionsPerFileMap = SuggestionHelper.groupSuggestionsPerFile(suggestions);
        SuggestionHelper.initSuggestionPerFileMap(suggestionsPerFileMap, suggestions);
    }

    @Override
    public void run() {
        try {
            applySuggestions();
        } catch (Exception e) {
            IncludatorPlugin.logStatus(new IncludatorStatus("Failed to apply includator suggestion text changes.", e), (String) null);
        }
    }

    private void applySuggestions() throws CoreException {
        CompositeChange change = new CompositeChange("Includator optimations.");
        for (Entry<IFile, Collection<Suggestion<?>>> curEntry : suggestionsPerFileMap.entrySet()) {
            IFile file = curEntry.getKey();
            try {
                change.add(getFileChange(file, curEntry.getValue()));
            } catch (Exception e) {
                String currentPath = FileHelper.uriToStringPath(file.getLocationURI());
                currentPath = FileHelper.getSmartFilePath(currentPath, file.getProject());
                IncludatorPlugin.logStatus(new IncludatorStatus("Failed to apply includator suggestion text changes.", e), currentPath);
            }
        }
        change.initializeValidationData(new NullProgressMonitor());
        PerformChangeOperation op = new PerformChangeOperation(change);
        op.setUndoManager(RefactoringCore.getUndoManager(), "Undo Includator change");
        op.run(new NullProgressMonitor());
    }

    private Change getFileChange(IFile file, Collection<Suggestion<?>> suggestions) {
        TextFileChange fileChange = new TextFileChange("Includator optimizations.", file);
        MultiTextEdit textEdit = new MultiTextEdit();
        for (Suggestion<?> curSuggestion : suggestions) {
            Change curChange = curSuggestion.getDefaultQuickFix().getChange(file);
            if (curChange instanceof DeleteFileChange) {
                return curChange;
            }
            List<TextEdit> extractedEdits = ChangeHelper.extractTextEdits(curChange);
            textEdit.addChildren(extractedEdits.toArray(new TextEdit[0]));
        }
        fileChange.setEdit(textEdit);
        return fileChange;
    }
}
