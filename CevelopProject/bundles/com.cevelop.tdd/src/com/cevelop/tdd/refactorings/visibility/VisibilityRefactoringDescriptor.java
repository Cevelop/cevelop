package com.cevelop.tdd.refactorings.visibility;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.VisibilityInfo;


public class VisibilityRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, VisibilityInfo> {

    protected VisibilityRefactoringDescriptor(final String project, final String description, final String comment, VisibilityInfo info) {
        super(RefactoringId.VISIBILITY, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new VisibilityRefactoring(getTranslationUnit(), info);
    }
}
