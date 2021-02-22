package com.cevelop.tdd.refactorings.create.variable.member;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.MemberVariableInfo;


public class CreateMemberVariableRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, MemberVariableInfo> {

    protected CreateMemberVariableRefactoringDescriptor(final String project, final String description, final String comment,
                                                        MemberVariableInfo info) {
        super(RefactoringId.CREATE_MEMBER_VARIABLE, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new CreateMemberVariableRefactoring(getTranslationUnit(), info);
    }
}
