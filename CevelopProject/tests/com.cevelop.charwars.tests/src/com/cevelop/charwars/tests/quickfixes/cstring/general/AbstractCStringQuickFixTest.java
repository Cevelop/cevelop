package com.cevelop.charwars.tests.quickfixes.cstring.general;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.charwars.constants.ProblemId;
import com.cevelop.charwars.quickfixes.cstring.general.CStringQuickFix;
import com.cevelop.charwars.tests.quickfixes.AbstractQuickFixTest;


public abstract class AbstractCStringQuickFixTest extends AbstractQuickFixTest {

    @Override
    protected ProblemId getProblemId() {
        return ProblemId.C_STRING_PROBLEM;
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new CStringQuickFix();
    }
}
