package com.cevelop.charwars.tests.quickfixes.cstring.parameter;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.charwars.constants.ProblemId;
import com.cevelop.charwars.quickfixes.cstring.parameter.CStringParameterQuickFix;
import com.cevelop.charwars.tests.quickfixes.AbstractQuickFixTest;


public class CStringParameterQuickFixTest extends AbstractQuickFixTest {

    @Override
    protected ProblemId getProblemId() {
        return ProblemId.C_STRING_PARAMETER_PROBLEM;
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new CStringParameterQuickFix();
    }
}
