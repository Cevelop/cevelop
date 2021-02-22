package com.cevelop.codeanalysator.autosar.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.autosar.quickfix.ParenthesizeLogicalOperatorsQuickFix;
import com.cevelop.codeanalysator.core.guideline.Rule;

public class ParenthesizeLogicalOperatorsQuickFixTest extends AutosarQuickFixTestBase{

   @Override
   protected Rule getRuleToQuickFix() {
      return AutosarGuideline.A05_02_06_ParenthesizeLogicalOperators;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new ParenthesizeLogicalOperatorsQuickFix("");
   }

}
