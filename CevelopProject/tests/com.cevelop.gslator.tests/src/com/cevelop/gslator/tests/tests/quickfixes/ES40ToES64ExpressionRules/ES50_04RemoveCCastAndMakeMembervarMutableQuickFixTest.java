package com.cevelop.gslator.tests.tests.quickfixes.ES40ToES64ExpressionRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules.ES50_02RemoveConstCastAndMakeMembervarMutableQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class ES50_04RemoveCCastAndMakeMembervarMutableQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new ES50_02RemoveConstCastAndMakeMembervarMutableQuickFix();
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_ES50;
    }
}
