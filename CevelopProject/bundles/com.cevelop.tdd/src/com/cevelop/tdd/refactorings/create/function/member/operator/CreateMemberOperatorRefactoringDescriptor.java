package com.cevelop.tdd.refactorings.create.function.member.operator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.MemberOperatorInfo;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;


public class CreateMemberOperatorRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, MemberOperatorInfo> {

    protected CreateMemberOperatorRefactoringDescriptor(final String project, final String description, final String comment,
                                                        final MemberOperatorInfo info) {
        super(RefactoringId.CREATE_MEMBER_OPERATOR, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new CreateMemberOperatorRefactoring(getTranslationUnit(), info);
    }
}
