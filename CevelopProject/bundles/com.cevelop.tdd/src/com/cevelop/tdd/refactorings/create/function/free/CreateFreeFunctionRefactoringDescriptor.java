package com.cevelop.tdd.refactorings.create.function.free;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.FreeFunctionInfo;


public class CreateFreeFunctionRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, FreeFunctionInfo> {

    protected CreateFreeFunctionRefactoringDescriptor(final String project, final String description, final String comment,
                                                      final FreeFunctionInfo info) {
        super(RefactoringId.CREATE_FREE_FUNCTION, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new CreateFreeFunctionRefactoring(getTranslationUnit(), info);
    }
}
