package com.cevelop.gslator.tests.tests.quickfixes.C20ToC22DefaultOperationsRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.quickfixes.SetAttributeQuickFix;
import com.cevelop.gslator.quickfixes.C20ToC22DefaultOperationsRules.C20RedundantOperationsQuickFix;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class C20RedundantOperationsQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_C20;
    }

    @Override
    protected IMarkerResolution getQuickFix() {
        if (setIgnoreAttributeQuickFix) {
            return new SetAttributeQuickFix();
        }
        return new C20RedundantOperationsQuickFix();
    }

}
