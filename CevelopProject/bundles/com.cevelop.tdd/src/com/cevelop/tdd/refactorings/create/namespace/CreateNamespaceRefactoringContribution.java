package com.cevelop.tdd.refactorings.create.namespace;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import ch.hsr.ifs.iltis.cpp.core.resources.info.IInfo;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.NamespaceInfo;
import com.cevelop.tdd.refactorings.AbstractTddRefactoringContribution;


public class CreateNamespaceRefactoringContribution extends AbstractTddRefactoringContribution {

    /**
     * {@inheritDoc}
     */
    @Override
    public RefactoringDescriptor createDescriptor(final RefactoringId id, final String project, final String description, final String comment,
            final Map<String, String> arguments, final int flags) throws IllegalArgumentException {

        if (id == RefactoringId.CREATE_NAMESPACE) {
            return new CreateNamespaceRefactoringDescriptor(project, description, comment, IInfo.fromMap(NamespaceInfo::new, arguments));
        }
        return null;
    }

}
