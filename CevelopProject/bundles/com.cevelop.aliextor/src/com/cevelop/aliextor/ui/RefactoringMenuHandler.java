package com.cevelop.aliextor.ui;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;

import com.cevelop.aliextor.refactoring.ProxyRefactoring;
import com.cevelop.aliextor.wizard.Wizard;


public class RefactoringMenuHandler extends WizardRefactoringStarterMenuHandler<Wizard, ProxyRefactoring> {

    @Override
    protected ProxyRefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new ProxyRefactoring(element, selection);
    }

    @Override
    protected Wizard getRefactoringWizard(ProxyRefactoring refactoring) {
        return new Wizard(refactoring, RefactoringWizard.DIALOG_BASED_USER_INTERFACE | RefactoringWizard.WIZARD_BASED_USER_INTERFACE);
    }

}
