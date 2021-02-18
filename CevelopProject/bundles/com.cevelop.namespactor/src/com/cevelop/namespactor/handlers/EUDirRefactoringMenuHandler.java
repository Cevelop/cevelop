package com.cevelop.namespactor.handlers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import com.cevelop.namespactor.refactoring.NSRefactoringWizard;
import com.cevelop.namespactor.refactoring.eudir.EUDirRefactoring;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;


public class EUDirRefactoringMenuHandler extends WizardRefactoringStarterMenuHandler<NSRefactoringWizard, EUDirRefactoring> {

    @Override
    protected EUDirRefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new EUDirRefactoring(element, selection);
    }

    @Override
    protected NSRefactoringWizard getRefactoringWizard(EUDirRefactoring refactoring) {
        return new NSRefactoringWizard(refactoring);
    }
}
