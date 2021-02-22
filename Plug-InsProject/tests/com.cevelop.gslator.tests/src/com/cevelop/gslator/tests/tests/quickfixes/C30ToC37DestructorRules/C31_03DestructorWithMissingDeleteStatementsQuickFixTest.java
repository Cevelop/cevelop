package com.cevelop.gslator.tests.tests.quickfixes.C30ToC37DestructorRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.quickfixes.C30ToC37DestructorRules.C31_03DestructorWithMissingDeleteStatementsQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C31_03DestructorWithMissingDeleteStatementsQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new C31_03DestructorWithMissingDeleteStatementsQuickFix();
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_C31_03;
    }

}
