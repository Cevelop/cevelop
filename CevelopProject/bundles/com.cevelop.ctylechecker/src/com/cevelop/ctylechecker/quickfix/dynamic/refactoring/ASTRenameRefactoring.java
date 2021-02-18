package com.cevelop.ctylechecker.quickfix.dynamic.refactoring;

import java.util.Optional;

import org.eclipse.cdt.internal.ui.refactoring.rename.CRefactoringArgument;
import org.eclipse.cdt.internal.ui.refactoring.rename.CRefactory;
import org.eclipse.cdt.internal.ui.refactoring.rename.CRenameProcessor;
import org.eclipse.cdt.internal.ui.refactoring.rename.CRenameRefactoringPreferences;
import org.eclipse.cdt.internal.ui.refactoring.rename.RenameSupport;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.quickfix.dynamic.model.MarkerModel;
import com.cevelop.ctylechecker.quickfix.dynamic.util.MarkerUtil;


@SuppressWarnings("restriction")
public class ASTRenameRefactoring extends AbstractRenameRefactoring {

    MarkerModel markerModel;

    public ASTRenameRefactoring(IMarker pMarker, IDocument pDocument) {
        this.markerModel = MarkerUtil.createModel(pMarker, pDocument);
    }

    private Boolean checkPreConditions() {
        return !markerModel.name.isEmpty() && markerModel.charStart < markerModel.charEnd && markerModel.file != null;
    }

    private CRenameProcessor prepareProcessor(String pReplacementText) throws BadLocationException {
        CRefactoringArgument arg = new CRefactoringArgument(markerModel.file, markerModel.charStart, markerModel.name.length());
        CRenameProcessor processor = new CRenameProcessor(CRefactory.getInstance(), arg);
        processor.setReplacementText(pReplacementText);
        CRenameRefactoringPreferences preferences = new CRenameRefactoringPreferences();
        processor.setSelectedOptions(preferences.getOptions());
        processor.setExhaustiveSearchScope(preferences.getScope());
        processor.setWorkingSetName(preferences.getWorkingSet());
        return processor;
    }

    public Boolean performFor(IRule pRule) throws Exception {
        if (checkPreConditions()) {
            CRenameProcessor processor = prepareProcessor(applyTransformations(pRule));
            RenameSupport support = RenameSupport.create(processor);
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (!hasNameChanged(processor) || processor.getReplacementText().isEmpty()) {

                return support.openDialog(window.getShell());
            } else {
                return support.perform(window.getShell(), window);
            }
        }
        return false;
    }

    public Boolean performDefault() throws Exception {
        return performFor(null);
    }

    private Boolean hasNameChanged(CRenameProcessor processor) {
        return !processor.getReplacementText().equals(markerModel.name);
    }

    @Override
    protected Optional<String> getOriginalName() {
        return Optional.ofNullable(markerModel.name);
    }
}
