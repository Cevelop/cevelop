package com.cevelop.tdd.refactorings.argument;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.ArgumentMismatchInfo;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;


public class ArgumentRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, ArgumentMismatchInfo> {

    protected ArgumentRefactoringDescriptor(final String project, final String description, final String comment, ArgumentMismatchInfo info) {
        super(RefactoringId.ARGUMENT, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new ArgumentRefactoring(getTranslationUnit(), info);
    }
}
