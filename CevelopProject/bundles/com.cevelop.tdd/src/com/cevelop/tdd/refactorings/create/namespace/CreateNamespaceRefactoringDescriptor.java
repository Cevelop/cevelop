package com.cevelop.tdd.refactorings.create.namespace;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.NamespaceInfo;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;


public class CreateNamespaceRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, NamespaceInfo> {

    protected CreateNamespaceRefactoringDescriptor(final String project, final String description, final String comment, NamespaceInfo info) {
        super(RefactoringId.CREATE_NAMESPACE, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new CreateNamespaceRefactoring(getTranslationUnit(), info);
    }
}
