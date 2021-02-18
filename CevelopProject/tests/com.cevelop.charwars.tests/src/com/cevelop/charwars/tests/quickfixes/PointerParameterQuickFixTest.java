package com.cevelop.charwars.tests.quickfixes;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.charwars.constants.ProblemId;
import com.cevelop.charwars.quickfixes.pointerparameter.PointerParameterQuickFix;


public class PointerParameterQuickFixTest extends AbstractQuickFixTest {

    @Override
    protected ProblemId getProblemId() {
        return ProblemId.POINTER_PARAMETER_PROBLEM;
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new PointerParameterQuickFix();
    }
}
