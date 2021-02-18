package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.VirtualFunctionShallHaveExactlyOneSpecifierQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class VirtualFunctionShallHaveExactlyOneSpecifierQuickFixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A10_03_01_VirtualFunctionShallHaveExactlyOneSpecifier;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new VirtualFunctionShallHaveExactlyOneSpecifierQuickFix("");
   }
}
