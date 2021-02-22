package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.SwitchMustHaveAtLeastTwoCasesQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class SwitchMustHaveAtLeastTwoCasesQuickFixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A06_04_01_SwitchMustHaveAtLeastTwoCases;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new SwitchMustHaveAtLeastTwoCasesQuickFix("");
   }
}
