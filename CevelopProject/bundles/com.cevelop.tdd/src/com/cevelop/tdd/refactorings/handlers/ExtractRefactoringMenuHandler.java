package com.cevelop.tdd.refactorings.handlers;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import com.cevelop.tdd.infos.ExtractToHeaderInfo;
import com.cevelop.tdd.refactorings.extract.ExtractToHeaderRefactoring;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.RefactoringStarterMenuHandler;


public class ExtractRefactoringMenuHandler extends RefactoringStarterMenuHandler<ExtractToHeaderRefactoring> {

    @Override
    protected ExtractToHeaderRefactoring getRefactoring(ICElement element, Optional<ITextSelection> selection) {
        return new ExtractToHeaderRefactoring(element, new ExtractToHeaderInfo().also(c -> c.setSelection(selection)));
    }
}
