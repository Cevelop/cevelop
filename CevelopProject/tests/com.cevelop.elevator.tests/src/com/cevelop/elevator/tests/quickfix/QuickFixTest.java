package com.cevelop.elevator.tests.quickfix;

import java.util.Properties;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.codan.core.model.IProblemProfile;
import org.eclipse.cdt.codan.core.param.IProblemPreferenceCompositeDescriptor;

import com.cevelop.elevator.checker.Preferences;
import com.cevelop.elevator.checker.Preferences.Preference;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingQuickfixTest;


public abstract class QuickFixTest extends CDTTestingQuickfixTest {

    protected void configureTest(Properties properties) {
        super.configureTest(properties);

        IProblemProfile profile = CodanRuntime.getInstance().getCheckersRegistry().getWorkspaceProfile();
        IProblem problem = profile.findProblem(getProblemId().getId());
        IProblemPreferenceCompositeDescriptor preferences = (IProblemPreferenceCompositeDescriptor) problem.getPreference();

        //@formatter:off
        properties.keySet().stream()
                  .filter(key -> key instanceof String)
                  .map(key -> (String) key)
                  .filter(key -> key.startsWith("preferences."))
                  .map(key -> key.replaceFirst("preferences.", ""))
                  .forEach(key -> {
                      if (!Preferences.Companion.getAll().containsKey(key)) {
                          throw new RuntimeException("Invalid checker preference key '" + key + "'!");
                      }
                      Preference<?> checkerPreference = Preferences.Companion.getAll().get(key);
                      Boolean value = Boolean.valueOf(properties.getProperty("preferences." + key));
                      preferences.getChildDescriptor(checkerPreference.getKey()).setValue(value);
                  });
        //@formatter:on
    }

    abstract protected IProblemId<?> getProblemId();
}
