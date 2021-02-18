package com.cevelop.gslator.tests.tests.quickfixes.ES70ToES86StatementRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.quickfixes.ES70ToES86StatementRules.ES76_03AvoidGotoUseBreakQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class ES76_03AvoidGotoUseBreakQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new ES76_03AvoidGotoUseBreakQuickFix();
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_ES76;
    }
}
