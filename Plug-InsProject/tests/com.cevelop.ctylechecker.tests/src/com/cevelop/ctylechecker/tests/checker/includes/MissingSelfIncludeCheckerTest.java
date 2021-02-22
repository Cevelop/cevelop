package com.cevelop.ctylechecker.tests.checker.includes;

import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;
import com.cevelop.ctylechecker.tests.checker.CheckerTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class MissingSelfIncludeCheckerTest extends CheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.MISSING_SELF_INCLUDE;
    }
}
