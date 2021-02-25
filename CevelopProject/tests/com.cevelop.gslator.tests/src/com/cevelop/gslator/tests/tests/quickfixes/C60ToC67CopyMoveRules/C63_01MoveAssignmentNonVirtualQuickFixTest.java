package com.cevelop.gslator.tests.tests.quickfixes.C60ToC67CopyMoveRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.quickfixes.C60ToC67CopyMoveRules.C63_01MoveAssignmentNonVirtualQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C63_01MoveAssignmentNonVirtualQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new C63_01MoveAssignmentNonVirtualQuickFix();
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_C63_01;
    }

}
