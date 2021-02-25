package com.cevelop.ctylechecker.tests.quickfix.includes;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;
import com.cevelop.ctylechecker.quickfix.includes.MissingUserIncludeQuickfix;
import com.cevelop.ctylechecker.tests.quickfix.QuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class MissingSelfIncludeQuickfixTest extends QuickFixTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.MISSING_SELF_INCLUDE;
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new MissingUserIncludeQuickfix();
    }
}
