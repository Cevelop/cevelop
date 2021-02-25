package com.cevelop.gslator.tests.tests.quickfixes.C80ToC89OtherDefaultOperationRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.quickfixes.C80ToC89OtherDefaultOperationRules.C85NamespaceLevelSwapFunctionQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C85NamespaceLevelSwapFunctionQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new C85NamespaceLevelSwapFunctionQuickFix();
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_C85;
    }
}
