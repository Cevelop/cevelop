package com.cevelop.gslator.tests.tests.checkers.C60ToC67CopyMoveRules;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.tests.tests.checkers.BaseCheckerTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C60_03CopyAssignmentReturnByNonConstRefCheckerTest extends BaseCheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_C60_03;
    }

}
