package com.cevelop.tdd.refactorings.create.variable.member;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.MemberVariableInfo;
import com.cevelop.tdd.refactorings.AbstractTddRefactoringContribution;

import ch.hsr.ifs.iltis.cpp.core.resources.info.IInfo;


public class CreateMemberVariableRefactoringContribution extends AbstractTddRefactoringContribution {

    /**
     * {@inheritDoc}
     */
    @Override
    public RefactoringDescriptor createDescriptor(final RefactoringId id, final String project, final String description, final String comment,
            final Map<String, String> arguments, final int flags) throws IllegalArgumentException {

        if (id == RefactoringId.CREATE_MEMBER_VARIABLE) {
            return new CreateMemberVariableRefactoringDescriptor(project, description, comment, IInfo.fromMap(MemberVariableInfo::new, arguments));
        }
        return null;
    }

}
