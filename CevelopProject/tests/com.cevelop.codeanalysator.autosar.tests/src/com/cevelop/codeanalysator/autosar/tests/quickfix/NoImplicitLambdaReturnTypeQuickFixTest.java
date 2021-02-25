package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.NoImplicitLambdaReturnTypeQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class NoImplicitLambdaReturnTypeQuickFixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A05_01_06_NoImplicitLambdaReturnType;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new NoImplicitLambdaReturnTypeQuickFix("");
   }
}
