package com.cevelop.charwars.tests.quickfixes;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.charwars.constants.ProblemId;
import com.cevelop.charwars.quickfixes.array.ArrayQuickFix;


public class ArrayQuickFixTest extends AbstractQuickFixTest {

    @Override
    protected ProblemId getProblemId() {
        return ProblemId.ARRAY_PROBLEM;
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new ArrayQuickFix();
    }
}
