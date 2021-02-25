package com.cevelop.elevator.ui.handlers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.RefactoringStarterMenuHandler;

import com.cevelop.elevator.refactoring.ElevateProjectRefactoring;


public class ElevateProjectHandler extends RefactoringStarterMenuHandler<ElevateProjectRefactoring> {

    @Override
    protected ElevateProjectRefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new ElevateProjectRefactoring(element, selection);
    }

}
