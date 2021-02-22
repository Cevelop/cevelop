package com.cevelop.elevator.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;
import org.junit.Test;

import com.cevelop.elevator.ids.IdHelper;
import com.cevelop.elevator.quickfix.NullMacroQuickFix;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class ReplaceNullMacroQuickFixTest extends QuickFixTest {

    @Override
    protected IProblemId<?> getProblemId() {
        return IdHelper.ProblemId.NULL_MACRO;
    }

    @Test
    public void runTest() throws Throwable {
        openPrimaryTestFileInEditor();
        runQuickfixForAllMarkersAndAssertAllEqual();
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new NullMacroQuickFix();
    }
}
