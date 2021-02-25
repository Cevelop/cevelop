package com.cevelop.constificator.refactorings;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;


public class MultiChangeRefactoringWizard extends RefactoringWizard {

    public MultiChangeRefactoringWizard(Refactoring refactoring) {
        super(refactoring, WIZARD_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
    }

    @Override
    protected void addUserInputPages() {}

}
