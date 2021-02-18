package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.DoNotOmitVirtSpecifierAddOverrideQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class DoNotOmitVirtSpecifierAddOverrideQuickFixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A10_03_02_DoNotOmitVirtSpecifier;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new DoNotOmitVirtSpecifierAddOverrideQuickFix("");
   }
}
