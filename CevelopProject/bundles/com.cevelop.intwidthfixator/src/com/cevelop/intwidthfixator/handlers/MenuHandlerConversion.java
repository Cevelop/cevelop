package com.cevelop.intwidthfixator.handlers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import com.cevelop.intwidthfixator.refactorings.conversion.ConversionInfo;
import com.cevelop.intwidthfixator.refactorings.conversion.ConversionRefactoring;
import com.cevelop.intwidthfixator.refactorings.wizards.ConversionRefactoringWizard;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class MenuHandlerConversion extends WizardRefactoringStarterMenuHandler<ConversionRefactoringWizard, ConversionRefactoring> {

    @Override
    protected ConversionRefactoringWizard getRefactoringWizard(ConversionRefactoring refactoring) {
        return new ConversionRefactoringWizard(refactoring);
    }

    @Override
    protected ConversionRefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new ConversionRefactoring(element, new ConversionInfo().also(i -> i.setSelection(selection)));
    }
}
