package com.cevelop.charwars.operation;

import org.eclipse.core.resources.IProject;

import com.cevelop.charwars.constants.ProblemId;

import ch.hsr.ifs.iltis.cpp.versionator.definition.CPPVersion;
import ch.hsr.ifs.iltis.cpp.versionator.definition.EnableCodanCheckers;
import ch.hsr.ifs.iltis.cpp.versionator.definition.IVersionModificationOperation;


public class EnableCStringAliasProblemOperation implements IVersionModificationOperation {

    @Override
    public void perform(IProject project, CPPVersion selectedVersion, boolean enabled) {
        EnableCodanCheckers.enableProblems(project, enabled, ProblemId.C_STRING_ALIAS_PROBLEM.getId());
        EnableCodanCheckers.setPreference_UseWorkspaceSettings(project, false);
    }
}
