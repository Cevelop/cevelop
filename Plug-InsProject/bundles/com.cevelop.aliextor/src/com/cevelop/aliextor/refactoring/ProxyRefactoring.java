package com.cevelop.aliextor.refactoring;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


public class ProxyRefactoring extends AliExtorRefactoring {

    public ProxyRefactoring(ICElement element, Optional<ITextSelection> selection) {
        super(element, selection);
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        init();

        refactorSelection.setStrategy(this);
        return refactorSelection.getStrategy().checkInitialConditions(initStatus);

    }

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        refactorSelection.getStrategy().collectModifications(collector.rewriterForTranslationUnit(ast));
    }

}
