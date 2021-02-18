package com.cevelop.codeanalysator.core.tests.quickfix;

import java.util.Properties;

import org.eclipse.cdt.codan.ui.ICodanMarkerResolution;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IMarkerResolution;
import org.junit.Test;

import com.cevelop.codeanalysator.core.CodeAnalysatorRuntime;
import com.cevelop.codeanalysator.core.guideline.Rule;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingQuickfixTestWithPreferences;


public abstract class CodeAnalysatorQuickFixTestBase extends CDTTestingQuickfixTestWithPreferences {

   private boolean setIgnoreAttributeQuickFix;

   @Override
   protected void configureTest(Properties properties) {
      super.configureTest(properties);

      setIgnoreAttributeQuickFix = Boolean.parseBoolean(properties.getProperty("setIgnoreAttribute", "false"));
   }

   @Override
   protected void initAdditionalIncludes() throws Exception {
      stageExternalIncludePathsForBothProjects("include/quickfix");
      super.initAdditionalIncludes();
   }

   protected abstract Rule getRuleToQuickFix();

   protected abstract IMarkerResolution getQuickFixToTest();

   @Override
   protected IProblemId<?> getProblemId() {
      Rule ruleToQuickFix = getRuleToQuickFix();
      return IProblemId.wrap(ruleToQuickFix.getProblemId());
   }

   @Override
   protected IMarkerResolution createMarkerResolution() {
      if (setIgnoreAttributeQuickFix) {
         return getRuleToQuickFix().getSuppressQuickFix();
      }

      return getQuickFixToTest();
   }

   @Override
   public IPreferenceStore initPrefs() {
      return CodeAnalysatorRuntime.getDefault().getPropAndPrefHelper().getWorkspacePreferences();
   }

   @Test
   public void runTest() throws Throwable {
      runQuickfixForAllMarkersAndAssertAllEqual();
   }

   @Override
   public void runQuickFix(final IMarker marker) {
      IMarkerResolution resolution = createMarkerResolution();
      if (resolution instanceof ICodanMarkerResolution) {
         if (!((ICodanMarkerResolution) resolution).isApplicable(marker)) { return; }
      }

      super.runQuickFix(marker);
   }
}
