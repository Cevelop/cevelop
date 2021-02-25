package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.MakeHexLiteralUppercaseQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;

public class MakeHexLiteralUppercaseQuickFixTest extends AutosarQuickFixTestBase{

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A02_13_05_HexValuesMustBeUppercase;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new MakeHexLiteralUppercaseQuickFix("");
   }

}
