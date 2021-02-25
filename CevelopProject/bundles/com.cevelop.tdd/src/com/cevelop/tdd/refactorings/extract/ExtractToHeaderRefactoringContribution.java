package com.cevelop.tdd.refactorings.extract;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import ch.hsr.ifs.iltis.cpp.core.resources.info.IInfo;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.ExtractToHeaderInfo;
import com.cevelop.tdd.refactorings.AbstractTddRefactoringContribution;


public class ExtractToHeaderRefactoringContribution extends AbstractTddRefactoringContribution {

    /**
     * {@inheritDoc}
     */
    @Override
    public RefactoringDescriptor createDescriptor(final RefactoringId id, final String project, final String description, final String comment,
            final Map<String, String> arguments, final int flags) throws IllegalArgumentException {
        if (id == RefactoringId.EXTRACT_TO_HEADER) {
            return new ExtractToHeaderRefactoringDescriptor(project, description, comment, IInfo.fromMap(ExtractToHeaderInfo::new, arguments));
        }
        return null;
    }

}
