package com.cevelop.constificator.tests.checker;

import java.util.Properties;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.codan.core.model.IProblemProfile;
import org.eclipse.cdt.codan.core.param.IProblemPreferenceCompositeDescriptor;
import org.junit.Test;

import com.cevelop.constificator.checkers.Preferences;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingCheckerTest;


public abstract class CheckerTest extends CDTTestingCheckerTest {

    @Override
    protected void configureTest(Properties properties) {

        IProblemProfile profile = CodanRuntime.getInstance().getCheckersRegistry().getWorkspaceProfile();
        IProblem problem = profile.findProblem(getProblemId().getId());
        IProblemPreferenceCompositeDescriptor preference = (IProblemPreferenceCompositeDescriptor) problem.getPreference();

        for (String key : Preferences.Companion.getALL_CHECK_OPTION_KEYS()) {
            if (!key.equals(enabledOption())) {
                preference.getChildDescriptor(key).setValue(false);
            }
        }

        super.configureTest(properties);
    }

    protected abstract String enabledOption();

    @Test
    public void runTest() throws Throwable {
        assertMarkerLines(expectedMarkerLinesFromProperties);
    }

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("include/checker");
        super.initAdditionalIncludes();
    }

}
