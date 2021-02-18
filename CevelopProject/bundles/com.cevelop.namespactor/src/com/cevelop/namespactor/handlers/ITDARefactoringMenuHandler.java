package com.cevelop.namespactor.handlers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import com.cevelop.namespactor.refactoring.NSRefactoringWizard;
import com.cevelop.namespactor.refactoring.itda.ITDARefactoring;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;


public class ITDARefactoringMenuHandler extends WizardRefactoringStarterMenuHandler<NSRefactoringWizard, ITDARefactoring> {

    @Override
    protected ITDARefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new ITDARefactoring(element, selection);
    }

    @Override
    protected NSRefactoringWizard getRefactoringWizard(ITDARefactoring refactoring) {
        return new NSRefactoringWizard(refactoring);
    }
}
