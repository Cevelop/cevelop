package com.cevelop.gslator.tests.tests.quickfixes.C80ToC89OtherDefaultOperationRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.quickfixes.C30ToC37DestructorRules.C37C44C66C84ShouldBeNoExceptQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C84MakeSwapNoExceptQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new C37C44C66C84ShouldBeNoExceptQuickFix();
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_C84;
    }
}
