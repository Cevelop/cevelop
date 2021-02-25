package com.cevelop.ctylechecker.tests.quickfix.includes;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;
import com.cevelop.ctylechecker.quickfix.includes.MissingStandardIncludeQuickfix;
import com.cevelop.ctylechecker.tests.quickfix.QuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class MissingStandardIncludeQuickfixTest extends QuickFixTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.MISSING_STD_INCLUDE;
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new MissingStandardIncludeQuickfix();
    }
}
