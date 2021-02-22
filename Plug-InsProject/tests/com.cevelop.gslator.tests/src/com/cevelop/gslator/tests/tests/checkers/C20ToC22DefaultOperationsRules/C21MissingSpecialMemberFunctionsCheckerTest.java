package com.cevelop.gslator.tests.tests.checkers.C20ToC22DefaultOperationsRules;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.tests.tests.checkers.BaseCheckerTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C21MissingSpecialMemberFunctionsCheckerTest extends BaseCheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.P_C21;
    }

}
