package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class SuppressAllGuidelinesQuickfixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A05_01_01_DoNotUseLiteralValues;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return getRuleToQuickFix().getSuppressAllQuickFix();
   }
}
