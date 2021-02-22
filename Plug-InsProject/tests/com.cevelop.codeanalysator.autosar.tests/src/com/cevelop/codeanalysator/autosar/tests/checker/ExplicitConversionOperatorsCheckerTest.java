package com.cevelop.codeanalysator.autosar.tests.checker;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class ExplicitConversionOperatorsCheckerTest extends AutosarCheckerTestBase {

   @Override
   protected Rule getRuleToCheck() {
      return AutosarGuideline.A13_05_02_ExplicitConversionOperators;
   }
}
