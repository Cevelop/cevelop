package com.cevelop.codeanalysator.core.quickassist.handler;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import com.cevelop.codeanalysator.core.quickassist.refactoring.StructClassSwitcherRefactoring;
import com.cevelop.codeanalysator.core.quickassist.util.CodeanalysatorWizard;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;


public class StructClassSwitcherRefactoringHandler extends WizardRefactoringStarterMenuHandler<CodeanalysatorWizard, StructClassSwitcherRefactoring> {

    private final ICPPASTCompositeTypeSpecifier compositeTypeSpecifier;

    public StructClassSwitcherRefactoringHandler() {
        this(null);
    }

    public StructClassSwitcherRefactoringHandler(ICPPASTCompositeTypeSpecifier compositeTypeSpecifier) {
        this.compositeTypeSpecifier = compositeTypeSpecifier;
    }

    @Override
    protected StructClassSwitcherRefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new StructClassSwitcherRefactoring(element, selection, compositeTypeSpecifier);
    }

    @Override
    protected CodeanalysatorWizard getRefactoringWizard(StructClassSwitcherRefactoring refactoring) {
        return new CodeanalysatorWizard(refactoring, CodeanalysatorWizard.CHECK_INITIAL_CONDITIONS_ON_OPEN |
                                                     CodeanalysatorWizard.DIALOG_BASED_USER_INTERFACE);
    }
}
