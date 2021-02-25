package com.cevelop.codeanalysator.core.quickassist.handler;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;

import com.cevelop.codeanalysator.core.quickassist.refactoring.OverriderRefactoring;
import com.cevelop.codeanalysator.core.quickassist.runnable.OverrideProposoalRunnable;
import com.cevelop.codeanalysator.core.quickassist.util.CodeanalysatorWizard;


public class OverriderRefactoringHandler extends WizardRefactoringStarterMenuHandler<CodeanalysatorWizard, OverriderRefactoring> {

    private OverrideProposoalRunnable runnable = null;

    public OverriderRefactoringHandler() {}

    public OverriderRefactoringHandler(OverrideProposoalRunnable runnable) {
        this.runnable = runnable;
    }

    @Override
    protected OverriderRefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new OverriderRefactoring(element, selection, runnable);
    }

    @Override
    protected CodeanalysatorWizard getRefactoringWizard(OverriderRefactoring refactoring) {
        return new CodeanalysatorWizard(refactoring, 0);
    }

}
