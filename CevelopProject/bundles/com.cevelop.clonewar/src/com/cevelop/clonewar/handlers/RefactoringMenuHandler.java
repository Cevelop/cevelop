package com.cevelop.clonewar.handlers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import com.cevelop.clonewar.refactorings.CloneWarRefactoring;
import com.cevelop.clonewar.view.CloneWarRefactoringWizard;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;


public class RefactoringMenuHandler extends WizardRefactoringStarterMenuHandler<CloneWarRefactoringWizard, CloneWarRefactoring> {

    @Override
    protected CloneWarRefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new CloneWarRefactoring(element, selection);
    }

    @Override
    protected CloneWarRefactoringWizard getRefactoringWizard(CloneWarRefactoring refactoring) {
        return new CloneWarRefactoringWizard(refactoring);
    }

}
