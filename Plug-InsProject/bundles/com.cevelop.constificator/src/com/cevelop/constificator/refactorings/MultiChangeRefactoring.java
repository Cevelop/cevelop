package com.cevelop.constificator.refactorings;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.constificator.core.util.ast.ASTRewriteCache;


public class MultiChangeRefactoring extends Refactoring {

    private final CompositeChange changes;

    public MultiChangeRefactoring(ASTRewriteCache cache) {
        changes = new CompositeChange("Additional const qualifications");
        CompositeChange compositeChange = (CompositeChange) cache.getChange();
        changes.merge(compositeChange);
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return null;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return RefactoringStatus.createInfoStatus("Found missing const qualification");
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return changes;
    }

    @Override
    public String getName() {
        return "Add missing const qualifications";
    }

}
