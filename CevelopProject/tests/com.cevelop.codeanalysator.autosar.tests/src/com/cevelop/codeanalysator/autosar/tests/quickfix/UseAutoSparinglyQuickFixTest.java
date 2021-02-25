package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.ReplaceAutoWithDeducedTypeQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class UseAutoSparinglyQuickFixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A07_01_05_UseAutoSparingly;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new ReplaceAutoWithDeducedTypeQuickFix("");
   }
}
