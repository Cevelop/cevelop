package com.cevelop.constificator.tests.checker.localvariables;

import com.cevelop.constificator.checkers.Markers;
import com.cevelop.constificator.checkers.Preferences;
import com.cevelop.constificator.tests.checker.CheckerTest;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public abstract class TestBase extends CheckerTest {

    @Override
    protected String enabledOption() {
        return Preferences.CHECK_LOCAL_VARIABLES_KEY;
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return IProblemId.wrap(Markers.MissingQualification);
    }

}
