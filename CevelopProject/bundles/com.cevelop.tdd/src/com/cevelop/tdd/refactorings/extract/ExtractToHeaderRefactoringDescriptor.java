package com.cevelop.tdd.refactorings.extract;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.ExtractToHeaderInfo;


/**
 * @author tstauber
 */
public class ExtractToHeaderRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, ExtractToHeaderInfo> {

    protected ExtractToHeaderRefactoringDescriptor(final String project, final String description, final String comment,
                                                   final ExtractToHeaderInfo info) {
        super(RefactoringId.EXTRACT_TO_HEADER, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new ExtractToHeaderRefactoring(getTranslationUnit(), info);
    }

}
