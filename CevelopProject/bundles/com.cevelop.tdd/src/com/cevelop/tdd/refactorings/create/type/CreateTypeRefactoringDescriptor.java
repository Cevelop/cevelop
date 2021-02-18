package com.cevelop.tdd.refactorings.create.type;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.TypeInfo;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;


public class CreateTypeRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, TypeInfo> {

    protected CreateTypeRefactoringDescriptor(final String project, final String description, final String comment, TypeInfo info) {
        super(RefactoringId.CREATE_TYPE, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new CreateTypeRefactoring(getTranslationUnit(), info);
    }
}
