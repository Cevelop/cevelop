package com.cevelop.elevator.tests.checker;

import org.junit.Test;

import com.cevelop.elevator.ids.IdHelper;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class NullMacroCheckerTest extends CheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.NULL_MACRO;
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
