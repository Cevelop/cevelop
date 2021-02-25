package com.cevelop.namespactor.handlers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;

import com.cevelop.namespactor.refactoring.NSRefactoringWizard;
import com.cevelop.namespactor.refactoring.iudir.IUDirRefactoring;


public class IUDirRefactoringMenuHandler extends WizardRefactoringStarterMenuHandler<NSRefactoringWizard, IUDirRefactoring> {

    @Override
    protected IUDirRefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new IUDirRefactoring(element, selection);
    }

    @Override
    protected NSRefactoringWizard getRefactoringWizard(IUDirRefactoring refactoring) {
        return new NSRefactoringWizard(refactoring);
    }
}
