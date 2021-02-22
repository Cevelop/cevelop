package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.UseBracedInitializationQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class UseBracedInitializationQuickFixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A08_05_02_UseBracedInitialization;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new UseBracedInitializationQuickFix("");
   }
}
