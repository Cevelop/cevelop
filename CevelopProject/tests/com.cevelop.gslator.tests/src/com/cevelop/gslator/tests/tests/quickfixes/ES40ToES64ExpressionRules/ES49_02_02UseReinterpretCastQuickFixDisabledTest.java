package com.cevelop.gslator.tests.tests.quickfixes.ES40ToES64ExpressionRules;

import org.eclipse.cdt.codan.core.param.RootProblemPreference;
import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.checkers.ES40ToES64ExpressionRules.ES49IfMustUseNamedCastChecker;
import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules.ES49_02UseReinterpretCastQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class ES49_02_02UseReinterpretCastQuickFixDisabledTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new ES49_02UseReinterpretCastQuickFix();
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_ES49;
    }

    @Override
    protected void problemPreferenceSetup(RootProblemPreference preference) {
        super.problemPreferenceSetup(preference);
        preference.setChildValue(ES49IfMustUseNamedCastChecker.PREF_ENABLE_REINTERPRET_QUICKFIX, false);
    }
}
