package com.cevelop.macronator.ui.handlers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.RefactoringStarterMenuHandler;

import com.cevelop.macronator.refactoring.ExpandMacroRefactoring;


public class ExpandMacroHandler extends RefactoringStarterMenuHandler<ExpandMacroRefactoring> {

    @Override
    protected ExpandMacroRefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new ExpandMacroRefactoring(element, selection);
    }

}
