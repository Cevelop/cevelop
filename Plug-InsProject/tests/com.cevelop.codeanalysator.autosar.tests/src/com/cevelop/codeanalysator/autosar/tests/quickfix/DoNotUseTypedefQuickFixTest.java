package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.DoNotUseTypedefQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class DoNotUseTypedefQuickFixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A07_01_06_DoNotUseTypedef;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new DoNotUseTypedefQuickFix("");
   }
}
