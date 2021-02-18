package com.cevelop.macronator.refactoring;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.internal.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.window.IShellProvider;

import ch.hsr.ifs.iltis.cpp.core.wrappers.RefactoringRunner;


@SuppressWarnings("restriction")
public class ExpandMacroRefactoringRunner extends RefactoringRunner {

    public ExpandMacroRefactoringRunner(ICElement element, Optional<ITextSelection> selection, IShellProvider shellProvider, ICProject cProject) {
        super(element, selection, shellProvider, cProject);
    }

    @Override
    public void run() {
        ExpandMacroRefactoring refactoring = new ExpandMacroRefactoring(element, selection);
        ExpandMacroWizard wizard = new ExpandMacroWizard(refactoring);
        run(wizard, refactoring, RefactoringSaveHelper.SAVE_REFACTORING);
    }
}
