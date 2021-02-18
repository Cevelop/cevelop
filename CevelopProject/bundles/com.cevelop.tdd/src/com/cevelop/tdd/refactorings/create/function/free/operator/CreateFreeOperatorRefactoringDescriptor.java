package com.cevelop.tdd.refactorings.create.function.free.operator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.FreeOperatorInfo;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;


public class CreateFreeOperatorRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, FreeOperatorInfo> {

    protected CreateFreeOperatorRefactoringDescriptor(final String project, final String description, final String comment,
                                                      final FreeOperatorInfo info) {
        super(RefactoringId.CREATE_FREE_OPERATOR, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new CreateFreeOperatorRefactoring(getTranslationUnit(), info);
    }
}
