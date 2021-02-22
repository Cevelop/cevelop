package com.cevelop.gslator.tests.tests.quickfixes.C40ToC52ConstructorRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.quickfixes.C40ToC52ConstructorRules.C46C164DeclareFunctionExplicitQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C46DeclareSingleCtorExplicitQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new C46C164DeclareFunctionExplicitQuickFix();
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_C46;
    }

}
