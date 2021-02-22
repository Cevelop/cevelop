package com.cevelop.ctylechecker.tests.checker.cute;

import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;
import com.cevelop.ctylechecker.tests.checker.CheckerTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class AssertCheckerTest extends CheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.MULTIPLE_ASSERTS;
    }
}
