package com.cevelop.tdd.refactorings.create.function.member;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.MemberFunctionInfo;
import com.cevelop.tdd.refactorings.AbstractTddRefactoringContribution;

import ch.hsr.ifs.iltis.cpp.core.resources.info.IInfo;


/**
 * @author tstauber
 */
public class CreateMemberFunctionRefactoringContribution extends AbstractTddRefactoringContribution {

    /**
     * {@inheritDoc}
     */
    @Override
    public RefactoringDescriptor createDescriptor(final RefactoringId id, final String project, final String description, final String comment,
            final Map<String, String> arguments, final int flags) throws IllegalArgumentException {

        if (id == RefactoringId.CREATE_MEMBER_FUNCTION) {
            return new CreateMemberFunctionRefactoringDescriptor(project, description, comment, IInfo.fromMap(MemberFunctionInfo::new, arguments));
        }
        return null;
    }
}
