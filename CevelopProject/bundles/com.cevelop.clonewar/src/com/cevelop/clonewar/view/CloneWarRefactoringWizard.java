package com.cevelop.clonewar.view;

import java.util.List;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;

import com.cevelop.clonewar.refactorings.CloneWarRefactoring;


/**
 * Refactoring wizard for the clonewar transformations.
 *
 * @author ythrier(at)hsr.ch
 */
public class CloneWarRefactoringWizard extends RefactoringWizard {

    private PageFactory pageFactory = new PageFactory();

    /**
     * {@inheritDoc}
     */
    public CloneWarRefactoringWizard(Refactoring refactoring) {
        super(refactoring, WIZARD_BASED_USER_INTERFACE);
        setForcePreviewReview(true);
    }

    /**
     * Helper method to get the exact refactoring (clonewar).
     *
     * @return Clonewar refactoring class.
     */
    private CloneWarRefactoring getCWRefactoring() {
        return (CloneWarRefactoring) getRefactoring();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addUserInputPages() {
        for (UserInputWizardPage page : getInputPages())
            addPage(page);
    }

    /**
     * Returns the input pages for the refactoring.
     *
     * @return List of input pages.
     */
    private List<UserInputWizardPage> getInputPages() {
        return pageFactory.createPagesFor(getCWRefactoring().getTransformation());
    }
}
