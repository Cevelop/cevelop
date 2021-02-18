package com.cevelop.charwars.dialogs;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.charwars.constants.ErrorMessages;


public class ErrorRefactoring extends Refactoring {

    private String msg;

    @Override
    public String getName() {
        return ErrorMessages.ALERT_BOX_TITLE;
    }

    public ErrorRefactoring(String msg) {
        this.msg = msg;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return RefactoringStatus.createFatalErrorStatus(msg);
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return null;
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return null;
    }
}
