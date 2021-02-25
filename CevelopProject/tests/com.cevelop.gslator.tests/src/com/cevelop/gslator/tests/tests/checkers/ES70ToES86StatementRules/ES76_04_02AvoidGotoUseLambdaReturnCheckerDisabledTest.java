package com.cevelop.gslator.tests.tests.checkers.ES70ToES86StatementRules;

import org.eclipse.cdt.codan.core.param.RootProblemPreference;

import com.cevelop.gslator.checkers.ES70toES86StatementRules.ES76AvoidGotoChecker;
import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.tests.tests.checkers.BaseCheckerTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class ES76_04_02AvoidGotoUseLambdaReturnCheckerDisabledTest extends BaseCheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_ES76;
    }

    @Override
    protected void problemPreferenceSetup(RootProblemPreference preference) {
        super.problemPreferenceSetup(preference);
        preference.setChildValue(ES76AvoidGotoChecker.PREF_MULTIBREAK, false);
    }
}
