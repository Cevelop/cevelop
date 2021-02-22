package com.cevelop.tdd.refactorings.create.function.constructor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.ConstructorInfo;


public class CreateConstructorRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, ConstructorInfo> {

    protected CreateConstructorRefactoringDescriptor(final String project, final String description, final String comment,
                                                     final ConstructorInfo info) {
        super(RefactoringId.CREATE_CONSTRUCTOR, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new CreateConstructorRefactoring(getTranslationUnit(), info);
    }
}
