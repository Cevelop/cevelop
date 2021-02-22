package com.cevelop.tdd.refactorings.create.function.free;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import ch.hsr.ifs.iltis.cpp.core.resources.info.IInfo;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.FreeFunctionInfo;
import com.cevelop.tdd.refactorings.AbstractTddRefactoringContribution;


/**
 * @author tstauber
 */
public class CreateFreeFunctionRefactoringContribution extends AbstractTddRefactoringContribution {

    /**
     * {@inheritDoc}
     */
    @Override
    public RefactoringDescriptor createDescriptor(final RefactoringId id, final String project, final String description, final String comment,
            final Map<String, String> arguments, final int flags) throws IllegalArgumentException {

        if (id == RefactoringId.CREATE_FREE_FUNCTION) {
            return new CreateFreeFunctionRefactoringDescriptor(project, description, comment, IInfo.fromMap(FreeFunctionInfo::new, arguments));
        }
        return null;
    }
}
