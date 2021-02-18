package com.cevelop.tdd.refactorings;

import org.eclipse.cdt.core.index.IIndexManager;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;


public class TddRefactoringContext extends CRefactoringContext {

    public TddRefactoringContext(CRefactoring refactoring) {
        super(refactoring);
    }

    @Override
    protected int getIndexOptions() {
        return super.getIndexOptions() | IIndexManager.ADD_EXTENSION_FRAGMENTS_EDITOR;
    }
}
