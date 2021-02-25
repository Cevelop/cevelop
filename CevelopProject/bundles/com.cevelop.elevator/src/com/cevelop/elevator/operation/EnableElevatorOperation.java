package com.cevelop.elevator.operation;

import org.eclipse.core.resources.IProject;

import ch.hsr.ifs.iltis.cpp.versionator.definition.CPPVersion;
import ch.hsr.ifs.iltis.cpp.versionator.definition.EnableCodanCheckers;
import ch.hsr.ifs.iltis.cpp.versionator.definition.IVersionModificationOperation;

import com.cevelop.elevator.ids.IdHelper;


public class EnableElevatorOperation implements IVersionModificationOperation {

    @Override
    public void perform(IProject project, CPPVersion selectedVersion, boolean enabled) {

        //TODO cleanup
        // EnableCodanCheckers.enableProblem(project, true, InitializationChecker.UNINITIALIZED_VAR);
        // EnableCodanCheckers.enableProblem(project, false, InitializationChecker.DEFAULT_CTOR);
        // EnableCodanCheckers.enableProblem(project, true, InitializationChecker.NULL_MACRO);

        EnableCodanCheckers.enableProblems(project, enabled, IdHelper.ProblemId.UNINITIALIZED_VAR.getId(), IdHelper.ProblemId.DEFAULT_CTOR.getId(),
                IdHelper.ProblemId.NULL_MACRO.getId());

        EnableCodanCheckers.setPreference_UseWorkspaceSettings(project, false);

    }

}
