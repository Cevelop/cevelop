package com.cevelop.gslator.tests.tests.quickfixes.C80ToC89OtherDefaultOperationRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.quickfixes.C80ToC89OtherDefaultOperationRules.C83_02SwapParamIsNotReferenceQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C83_02SwapParamIsNotReferenceQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new C83_02SwapParamIsNotReferenceQuickFix();
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_C83;
    }
}
