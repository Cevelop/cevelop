package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.MandatoryLambdaParameterListQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class MandatoryLambdaParameterListQuickFixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A05_01_03_MandatoryLambdaParameterList;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new MandatoryLambdaParameterListQuickFix("");
   }
}
