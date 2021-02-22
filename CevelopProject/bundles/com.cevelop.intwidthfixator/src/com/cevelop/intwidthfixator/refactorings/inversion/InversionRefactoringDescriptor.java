package com.cevelop.intwidthfixator.refactorings.inversion;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;

import com.cevelop.intwidthfixator.helpers.IdHelper.RefactoringId;


/**
 * @author tstauber
 */
public class InversionRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, InversionRefactoringInfo> {

    protected InversionRefactoringDescriptor(final String project, final String description, final String comment,
                                             final InversionRefactoringInfo info) {
        super(RefactoringId.INVERSION, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new InversionRefactoring(getTranslationUnit(), info);
    }
}
