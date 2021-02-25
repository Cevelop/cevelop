package com.cevelop.gslator.tests.tests.quickfixes.C60ToC67CopyMoveRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.quickfixes.C60ToC67CopyMoveRules.C60_02CopyAssignmentParameterByConstRefQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C60_02CopyAssignmentParameterByConstRefQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new C60_02CopyAssignmentParameterByConstRefQuickFix();
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_C60_02;
    }

}
