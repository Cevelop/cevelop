package com.cevelop.intwidthfixator.refactorings.wizards;

import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.cevelop.intwidthfixator.refactorings.inversion.InversionRefactoring;


/**
 * @author tstauber
 */
public class InversionRefactoringWizard extends RefactoringWizard {

    public InversionRefactoringWizard(final InversionRefactoring refactoring) {
        super(refactoring, RefactoringWizard.DIALOG_BASED_USER_INTERFACE | RefactoringWizard.PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(Messages.InversionRefactoringWizard_S_Title);
        setDialogSettings(CUIPlugin.getDefault().getDialogSettings());
    }

    @Override
    protected void addUserInputPages() {
        addPage(new InversionRefactoringInputPage());
    }

}
