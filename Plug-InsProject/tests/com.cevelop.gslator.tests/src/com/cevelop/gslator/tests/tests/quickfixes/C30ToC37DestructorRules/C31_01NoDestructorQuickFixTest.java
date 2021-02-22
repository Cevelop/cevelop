package com.cevelop.gslator.tests.tests.quickfixes.C30ToC37DestructorRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.quickfixes.C30ToC37DestructorRules.C31_01NoDestructorQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C31_01NoDestructorQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new C31_01NoDestructorQuickFix();
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_C31_01;
    }

}
