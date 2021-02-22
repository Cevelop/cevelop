package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.ReplaceAutoWithDeducedTypeQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class DoNotInitializeAutoUsingInitializerListQuickFixTest extends AutosarQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A08_05_03_DoNotInitializeAutoUsingInitializerList;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new ReplaceAutoWithDeducedTypeQuickFix("");
   }
}
