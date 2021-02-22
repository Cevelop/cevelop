package com.cevelop.intwidthfixator.refactorings.conversion;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringDescriptor;

import com.cevelop.intwidthfixator.helpers.IdHelper.RefactoringId;


/**
 * @author tstauber
 */
public class ConversionRefactoringDescriptor extends CRefactoringDescriptor<RefactoringId, ConversionInfo> {

    protected ConversionRefactoringDescriptor(final String project, final String description, final String comment, final ConversionInfo info) {
        super(RefactoringId.CONVERSION, project, description, comment, RefactoringDescriptor.MULTI_CHANGE, info);
    }

    @Override
    public CRefactoring createRefactoring(final RefactoringStatus status) throws CoreException {
        return new ConversionRefactoring(getTranslationUnit(), info);
    }
}
