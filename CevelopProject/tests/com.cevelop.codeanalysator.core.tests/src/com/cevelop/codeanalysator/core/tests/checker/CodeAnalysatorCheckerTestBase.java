package com.cevelop.codeanalysator.core.tests.checker;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Test;

import com.cevelop.codeanalysator.core.CodeAnalysatorRuntime;
import com.cevelop.codeanalysator.core.guideline.Rule;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingCheckerTestWithPreferences;


public abstract class CodeAnalysatorCheckerTestBase extends CDTTestingCheckerTestWithPreferences {

   @Override
   protected void initAdditionalIncludes() throws Exception {
      stageExternalIncludePathsForBothProjects("include/checker");
      super.initAdditionalIncludes();
   }

   protected abstract Rule getRuleToCheck();

   @Override
   protected IProblemId<?> getProblemId() {
      Rule ruleToCheck = getRuleToCheck();
      return IProblemId.wrap(ruleToCheck.getProblemId());
   }

   @Override
   public IPreferenceStore initPrefs() {
      return CodeAnalysatorRuntime.getDefault().getPropAndPrefHelper().getWorkspacePreferences();
   }

   @Test
   public void runTest() throws Throwable {
      if (expectedMarkerLinesFromProperties == null) {
         assertMarkerLines();
      } else {
         assertMarkerLines(expectedMarkerLinesFromProperties);
      }
   }
}
