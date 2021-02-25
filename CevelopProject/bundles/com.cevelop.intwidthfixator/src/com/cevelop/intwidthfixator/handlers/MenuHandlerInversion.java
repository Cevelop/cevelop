package com.cevelop.intwidthfixator.handlers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;

import com.cevelop.intwidthfixator.refactorings.inversion.InversionRefactoring;
import com.cevelop.intwidthfixator.refactorings.inversion.InversionRefactoringInfo;
import com.cevelop.intwidthfixator.refactorings.wizards.InversionRefactoringWizard;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class MenuHandlerInversion extends WizardRefactoringStarterMenuHandler<InversionRefactoringWizard, InversionRefactoring> {

    @Override
    protected InversionRefactoringWizard getRefactoringWizard(InversionRefactoring refactoring) {
        return new InversionRefactoringWizard(refactoring);
    }

    @Override
    protected InversionRefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new InversionRefactoring(element, new InversionRefactoringInfo().also(i -> i.setSelection(selection)));
    }
}
