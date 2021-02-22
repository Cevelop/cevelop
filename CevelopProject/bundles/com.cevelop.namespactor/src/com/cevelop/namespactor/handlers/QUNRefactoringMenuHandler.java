package com.cevelop.namespactor.handlers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;

import com.cevelop.namespactor.refactoring.NSRefactoringWizard;
import com.cevelop.namespactor.refactoring.qun.QUNRefactoring;


public class QUNRefactoringMenuHandler extends WizardRefactoringStarterMenuHandler<NSRefactoringWizard, QUNRefactoring> {

    @Override
    protected QUNRefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new QUNRefactoring(element, selection);
    }

    @Override
    protected NSRefactoringWizard getRefactoringWizard(QUNRefactoring refactoring) {
        return new NSRefactoringWizard(refactoring);
    }
}
