package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.DoNotIntroduceVirtualFunctionInFinalClassQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class DoNotIntroduceVirtualFunctionInFinalClassQuickFixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A10_03_03_DoNotIntroduceVirtualFunctionInFinalClass;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new DoNotIntroduceVirtualFunctionInFinalClassQuickFix("");
   }
}
