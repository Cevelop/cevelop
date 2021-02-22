package com.cevelop.tdd.refactorings.create.type;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import ch.hsr.ifs.iltis.cpp.core.resources.info.IInfo;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.TypeInfo;
import com.cevelop.tdd.refactorings.AbstractTddRefactoringContribution;


public class CreateTypeRefactoringContribution extends AbstractTddRefactoringContribution {

    /**
     * {@inheritDoc}
     */
    @Override
    public RefactoringDescriptor createDescriptor(final RefactoringId id, final String project, final String description, final String comment,
            final Map<String, String> arguments, final int flags) throws IllegalArgumentException {

        if (id == RefactoringId.CREATE_TYPE) {
            return new CreateTypeRefactoringDescriptor(project, description, comment, IInfo.fromMap(TypeInfo::new, arguments));
        }
        return null;
    }

}
