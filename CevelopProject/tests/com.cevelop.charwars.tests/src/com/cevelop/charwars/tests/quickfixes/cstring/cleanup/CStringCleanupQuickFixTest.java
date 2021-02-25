package com.cevelop.charwars.tests.quickfixes.cstring.cleanup;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.charwars.constants.ProblemId;
import com.cevelop.charwars.quickfixes.cstring.cleanup.CStringCleanupQuickFix;
import com.cevelop.charwars.tests.quickfixes.AbstractQuickFixTest;


public class CStringCleanupQuickFixTest extends AbstractQuickFixTest {

    @Override
    protected ProblemId getProblemId() {
        return ProblemId.C_STRING_CLEANUP_PROBLEM;
    }

    @Override
    protected IMarkerResolution createMarkerResolution() {
        return new CStringCleanupQuickFix();
    }
}
