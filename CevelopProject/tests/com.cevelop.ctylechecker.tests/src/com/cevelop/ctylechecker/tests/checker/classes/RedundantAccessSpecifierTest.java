package com.cevelop.ctylechecker.tests.checker.classes;

import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;
import com.cevelop.ctylechecker.tests.checker.CheckerTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class RedundantAccessSpecifierTest extends CheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.REDUNDANT_ACCESS_SPECIFIER;
    }
}
