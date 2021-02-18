package com.cevelop.codeanalysator.core.quickassist.util;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.CRefactoringWizard;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;


public class CodeanalysatorWizard extends CRefactoringWizard {

    public CodeanalysatorWizard(CRefactoring refactoring, int flags) {
        super(refactoring, flags);
    }

    @Override
    protected void addUserInputPages() {}

}
