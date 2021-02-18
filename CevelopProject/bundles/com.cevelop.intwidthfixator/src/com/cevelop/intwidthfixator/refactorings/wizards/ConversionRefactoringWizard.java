package com.cevelop.intwidthfixator.refactorings.wizards;

import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.cevelop.intwidthfixator.refactorings.conversion.ConversionRefactoring;


/**
 * @author tstauber
 */
public class ConversionRefactoringWizard extends RefactoringWizard {

    public ConversionRefactoringWizard(final ConversionRefactoring refactoring) {
        super(refactoring, RefactoringWizard.DIALOG_BASED_USER_INTERFACE | RefactoringWizard.PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(Messages.ConversionRefactoringWizard_S_Title);
        setDialogSettings(CUIPlugin.getDefault().getDialogSettings());
    }

    @Override
    protected void addUserInputPages() {
        addPage(new ConversionRefactoringInputPage());
    }

}
