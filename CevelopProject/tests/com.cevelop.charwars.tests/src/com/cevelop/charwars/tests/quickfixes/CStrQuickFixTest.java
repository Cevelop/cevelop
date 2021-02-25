package com.cevelop.charwars.tests.quickfixes;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.charwars.constants.ProblemId;
import com.cevelop.charwars.quickfixes.cstr.CStrQuickFix;


public class CStrQuickFixTest extends AbstractQuickFixTest {

    @Override
    protected ProblemId getProblemId() {
        return ProblemId.C_STR_PROBLEM;
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new CStrQuickFix();
    }
}
