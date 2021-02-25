package com.cevelop.elevator.tests.checker;

import org.junit.Test;

import com.cevelop.elevator.ids.IdHelper;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class InitializationCheckerPositiveMatchesTest extends CheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.UNINITIALIZED_VAR;
    }

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("include");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        assertMarkerLines(expectedMarkerLinesFromProperties);
    }

}
