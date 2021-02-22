package com.cevelop.tdd.refactorings.create.function.member;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.MemberFunctionInfo;


public class CreateMemberFunctionRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, MemberFunctionInfo> {

    protected CreateMemberFunctionRefactoringDescriptor(final String project, final String description, final String comment,
                                                        final MemberFunctionInfo info) {
        super(RefactoringId.CREATE_MEMBER_FUNCTION, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new CreateMemberFunctionRefactoring(getTranslationUnit(), info);
    }
}
