package com.cevelop.gslator.tests.tests.quickfixes.ES05ToES34DeclarationRules;

import static org.junit.Assert.fail;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.ids.IdHelper;
import com.cevelop.gslator.tests.tests.quickfixes.BaseQuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class ES09AvoidALLCAPSnamesIgnoreQuickFixTest extends BaseQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        fail("ES.09 Has No QuickFix! Only Set Ignore Quick Fix Supported! Check Tests!");
        return null;
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.P_ES09;
    }

}
