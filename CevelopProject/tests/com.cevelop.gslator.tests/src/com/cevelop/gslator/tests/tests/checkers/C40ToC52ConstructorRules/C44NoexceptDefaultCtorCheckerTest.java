package com.cevelop.gslator.tests.tests.checkers.C40ToC52ConstructorRules;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.tests.tests.checkers.BaseCheckerTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C44NoexceptDefaultCtorCheckerTest extends BaseCheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_C44;
    }

}
