package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.DoNotImplicitlyCaptureVariablesQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class DoNotImplicitlyCaptureVariablesQuickFixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A05_01_02_DoNotImplicitlyCaptureVariables;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new DoNotImplicitlyCaptureVariablesQuickFix("");
   }
}
