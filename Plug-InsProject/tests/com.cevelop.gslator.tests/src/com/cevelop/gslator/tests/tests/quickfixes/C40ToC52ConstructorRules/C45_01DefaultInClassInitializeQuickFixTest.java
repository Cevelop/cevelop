package com.cevelop.gslator.tests.tests.quickfixes.C40ToC52ConstructorRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.quickfixes.C40ToC52ConstructorRules.C45_01DefaultInClassInitializeQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C45_01DefaultInClassInitializeQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new C45_01DefaultInClassInitializeQuickFix();
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_C45;
    }

}
