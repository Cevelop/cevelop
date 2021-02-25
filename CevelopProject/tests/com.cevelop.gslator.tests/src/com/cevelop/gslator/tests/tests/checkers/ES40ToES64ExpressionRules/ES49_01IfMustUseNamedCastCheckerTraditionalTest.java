package com.cevelop.gslator.tests.tests.checkers.ES40ToES64ExpressionRules;

import org.eclipse.cdt.codan.core.param.RootProblemPreference;

import com.cevelop.gslator.checkers.ES40ToES64ExpressionRules.ES49IfMustUseNamedCastChecker;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.tests.tests.checkers.BaseCheckerTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class ES49_01IfMustUseNamedCastCheckerTraditionalTest extends BaseCheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.P_ES49;
    }

    @Override
    protected void problemPreferenceSetup(RootProblemPreference preference) {
        super.problemPreferenceSetup(preference);
        preference.setChildValue(ES49IfMustUseNamedCastChecker.PREF_ENABLE_TRADITIONALCAST, true);
        preference.setChildValue(ES49IfMustUseNamedCastChecker.PREF_ENABLE_TYPECONST_WITH_CONSTINIT, false);
    }
}
