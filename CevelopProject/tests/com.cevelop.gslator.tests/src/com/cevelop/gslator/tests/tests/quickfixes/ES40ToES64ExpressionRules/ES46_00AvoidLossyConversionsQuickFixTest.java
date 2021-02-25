package com.cevelop.gslator.tests.tests.quickfixes.ES40ToES64ExpressionRules;

import org.eclipse.cdt.codan.core.param.RootProblemPreference;

import com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules.utils.ES46LossyType;
import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public abstract class ES46_00AvoidLossyConversionsQuickFixTest extends BaseQuickFixTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    protected abstract ES46LossyType getLossyType();

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_ES46;
    }

    @Override
    protected void problemPreferenceSetup(RootProblemPreference preference) {
        for (ES46LossyType type : ES46LossyType.values()) {
            if (type != ES46LossyType.UNKNOWN && type != getLossyType()) preference.setChildValue(type.toString(), false);
            if (type == getLossyType()) preference.setChildValue(type.toString(), true);
        }
        super.problemPreferenceSetup(preference);
    }
}
