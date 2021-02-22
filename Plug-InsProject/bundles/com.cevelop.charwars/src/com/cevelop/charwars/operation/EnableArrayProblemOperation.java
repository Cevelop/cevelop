package com.cevelop.charwars.operation;

import org.eclipse.core.resources.IProject;

import ch.hsr.ifs.iltis.cpp.versionator.definition.CPPVersion;
import ch.hsr.ifs.iltis.cpp.versionator.definition.EnableCodanCheckers;
import ch.hsr.ifs.iltis.cpp.versionator.definition.IVersionModificationOperation;

import com.cevelop.charwars.constants.ProblemId;


public class EnableArrayProblemOperation implements IVersionModificationOperation {

    @Override
    public void perform(IProject project, CPPVersion selectedVersion, boolean enabled) {
        EnableCodanCheckers.enableProblems(project, enabled, ProblemId.ARRAY_PROBLEM.getId());
        EnableCodanCheckers.setPreference_UseWorkspaceSettings(project, false);
    }
}
