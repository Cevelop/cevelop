package com.cevelop.charwars.dialogs;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;


public class ErrorRefactoringWizard extends RefactoringWizard {

    public ErrorRefactoringWizard(Refactoring refactoring, int flags) {
        super(refactoring, flags);
    }

    @Override
    protected void addUserInputPages() {
        //only display dialog box
    }
}
