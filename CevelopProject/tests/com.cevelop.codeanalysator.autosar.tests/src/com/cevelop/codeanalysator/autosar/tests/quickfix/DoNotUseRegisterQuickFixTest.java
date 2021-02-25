package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.DoNotUseRegisterQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class DoNotUseRegisterQuickFixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A07_01_04_DoNotUseRegister;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new DoNotUseRegisterQuickFix("");
   }
}
