package com.cevelop.elevator.refactoring;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.internal.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.RefactoringRunner;


@SuppressWarnings("restriction")
public class ElevateProjectRunner extends RefactoringRunner {

    public ElevateProjectRunner(ICElement element, Optional<ITextSelection> selection, IShellProvider shellProvider, ICProject cProject) {
        super(element, selection, shellProvider, cProject);
    }

    @Override
    public void run() {
        CRefactoring refactoring = new ElevateProjectRefactoring(element, selection);
        RefactoringWizard wizard = new RefactoringWizard(refactoring, RefactoringWizard.WIZARD_BASED_USER_INTERFACE |
                                                                      RefactoringWizard.WIZARD_BASED_USER_INTERFACE) {

            @Override
            protected void addUserInputPages() {}
        };

        wizard.setDefaultPageTitle("Elevate Project");
        run(wizard, refactoring, RefactoringSaveHelper.SAVE_REFACTORING);
    }
}
