package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.DoNotOmitVirtSpecifierAddFinalQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class DoNotOmitVirtSpecifierAddFinalQuickFixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A10_03_02_DoNotOmitVirtSpecifier;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new DoNotOmitVirtSpecifierAddFinalQuickFix("");
   }
}
