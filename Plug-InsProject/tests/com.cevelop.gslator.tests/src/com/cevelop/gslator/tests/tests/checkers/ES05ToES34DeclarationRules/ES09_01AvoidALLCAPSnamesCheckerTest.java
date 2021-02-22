package com.cevelop.gslator.tests.tests.checkers.ES05ToES34DeclarationRules;

import org.eclipse.cdt.codan.core.param.RootProblemPreference;

import com.cevelop.gslator.checkers.ES05ToES34DeclarationRules.ES09AvoidALLCAPSnamesChecker;
import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.tests.tests.checkers.BaseCheckerTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class ES09_01AvoidALLCAPSnamesCheckerTest extends BaseCheckerTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_ES09;
    }

    @Override
    protected void problemPreferenceSetup(RootProblemPreference preference) {
        super.problemPreferenceSetup(preference);
        preference.setChildValue(ES09AvoidALLCAPSnamesChecker.PREF_MARK_MACROS, false);
    }
}
