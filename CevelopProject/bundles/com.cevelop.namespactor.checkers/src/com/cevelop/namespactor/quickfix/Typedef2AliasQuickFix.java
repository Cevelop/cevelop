package com.cevelop.namespactor.quickfix;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;

import com.cevelop.namespactor.handlers.TD2ARefactoringMenuHandler;


public class Typedef2AliasQuickFix extends AbstractMarkerResolution {

    @Override
    public String getLabel() {
        return "Convert typedef to alias";
    }

    @Override
    protected WizardRefactoringStarterMenuHandler<?, ?> getRefactoringHandler() {
        return new TD2ARefactoringMenuHandler();
    }

}
