package com.cevelop.gslator.tests.tests.checkers.C80ToC89OtherDefaultOperationRules;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.tests.tests.checkers.BaseCheckerTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C83ValueLikeTypesShouldHaveSwapCheckerTest extends BaseCheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_C83;
    }
}
