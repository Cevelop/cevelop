package com.cevelop.tdd.refactorings.create.variable.local;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.LocalVariableInfo;


public class CreateLocalVariableRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, LocalVariableInfo> {

    private final LocalVariableInfo info;

    protected CreateLocalVariableRefactoringDescriptor(final String project, final String description, final String comment, LocalVariableInfo info) {
        super(RefactoringId.CREATE_LOCAL_VARIABLE, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
        this.info = info;
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new CreateLocalVariableRefactoring(getTranslationUnit(), info);
    }
}
