package com.cevelop.namespactor.handlers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;

import com.cevelop.namespactor.refactoring.NSRefactoringWizard;
import com.cevelop.namespactor.refactoring.iu.IURefactoring;


public class IURefactoringMenuHandler extends WizardRefactoringStarterMenuHandler<NSRefactoringWizard, IURefactoring> {

    @Override
    protected IURefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new IURefactoring(element, selection);
    }

    @Override
    protected NSRefactoringWizard getRefactoringWizard(IURefactoring refactoring) {
        return new NSRefactoringWizard(refactoring);
    }
}
