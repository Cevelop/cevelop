package com.cevelop.namespactor.handlers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;

import com.cevelop.namespactor.refactoring.NSRefactoringWizard;
import com.cevelop.namespactor.refactoring.td2a.TD2ARefactoring;


public class TD2ARefactoringMenuHandler extends WizardRefactoringStarterMenuHandler<NSRefactoringWizard, TD2ARefactoring> {

    @Override
    protected TD2ARefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new TD2ARefactoring(element, selection);
    }

    @Override
    protected NSRefactoringWizard getRefactoringWizard(TD2ARefactoring refactoring) {
        return new NSRefactoringWizard(refactoring);
    }
}
