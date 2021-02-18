package com.cevelop.intwidthfixator.refactorings.inversion;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import com.cevelop.intwidthfixator.helpers.IdHelper.RefactoringId;

import ch.hsr.ifs.iltis.core.core.functional.functions.Function;

import ch.hsr.ifs.iltis.cpp.core.resources.info.IInfo;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContribution;


/**
 * @author tstauber
 */
public class InversionRefactoringContribution extends CRefactoringContribution<RefactoringId> {

    /**
     * {@inheritDoc}
     */
    @Override
    public RefactoringDescriptor createDescriptor(final RefactoringId id, final String project, final String description, final String comment,
            final Map<String, String> arguments, final int flags) throws IllegalArgumentException {
        if (id == RefactoringId.INVERSION) {
            return new InversionRefactoringDescriptor(project, description, comment, IInfo.fromMap(InversionRefactoringInfo::new, arguments));
        }
        return null;
    }

    @Override
    protected Function<String, RefactoringId> getFromStringMethod() {
        return RefactoringId::of;
    }
}
