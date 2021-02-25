package com.cevelop.namespactor.handlers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;

import com.cevelop.namespactor.refactoring.NSRefactoringWizard;
import com.cevelop.namespactor.refactoring.iudec.IUDecRefactoring;


public class IUDecRefactoringMenuHandler extends WizardRefactoringStarterMenuHandler<NSRefactoringWizard, IUDecRefactoring> {

    @Override
    protected IUDecRefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new IUDecRefactoring(element, selection);
    }

    @Override
    protected NSRefactoringWizard getRefactoringWizard(IUDecRefactoring refactoring) {
        return new NSRefactoringWizard(refactoring);
    }
}
