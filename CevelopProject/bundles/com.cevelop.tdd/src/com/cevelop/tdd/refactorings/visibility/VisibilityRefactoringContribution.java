package com.cevelop.tdd.refactorings.visibility;

import java.util.Map;

import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import ch.hsr.ifs.iltis.cpp.core.resources.info.IInfo;

import com.cevelop.tdd.helpers.IdHelper.RefactoringId;
import com.cevelop.tdd.infos.VisibilityInfo;
import com.cevelop.tdd.refactorings.AbstractTddRefactoringContribution;


/**
 * @author tstauber
 */
public class VisibilityRefactoringContribution extends AbstractTddRefactoringContribution {

    /**
     * {@inheritDoc}
     */
    @Override
    public RefactoringDescriptor createDescriptor(final RefactoringId id, final String project, final String description, final String comment,
            final Map<String, String> arguments, final int flags) throws IllegalArgumentException {

        if (id == RefactoringId.VISIBILITY) {
            return new VisibilityRefactoringDescriptor(project, description, comment, IInfo.fromMap(VisibilityInfo::new, arguments));
        }
        return null;
    }
}
