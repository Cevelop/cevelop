package com.cevelop.gslator.tests.tests.quickfixes.C20ToC22DefaultOperationsRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.quickfixes.C20ToC22DefaultOperationsRules.C21MissingSpecialMemberFunctionsQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C21MissingSpecialMemberFunctionsQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new C21MissingSpecialMemberFunctionsQuickFix();
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.P_C21;
    }

}
