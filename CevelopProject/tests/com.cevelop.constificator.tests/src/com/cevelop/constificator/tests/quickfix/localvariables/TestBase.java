package com.cevelop.constificator.tests.quickfix.localvariables;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.constificator.checkers.Markers;
import com.cevelop.constificator.checkers.Preferences;
import com.cevelop.constificator.resolution.ConstificatorQuickFix;
import com.cevelop.constificator.tests.quickfix.QuickFixTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public abstract class TestBase extends QuickFixTest {

    @Override
    protected String enabledOption() {
        return Preferences.CHECK_LOCAL_VARIABLES_KEY;
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IProblemId.wrap(Markers.MissingQualification);
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new ConstificatorQuickFix().setTesting(true);
    }

}
