package com.cevelop.macronator.refactoring;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;


public class ExpandMacroWizard extends RefactoringWizard {

    public ExpandMacroWizard(Refactoring refactoring) {
        super(refactoring, WIZARD_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle("Expand Macro");
    }

    @Override
    protected void addUserInputPages() {
        // do nothing
    }
}
