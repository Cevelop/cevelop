package com.cevelop.codeanalysator.misra.tests.checker;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.misra.guideline.MisraGuideline;


public abstract class AvoidLossyConversionCheckerTest extends MisraCheckerTestBase {

   @Override
   protected Rule getRuleToCheck() {
      return MisraGuideline.M05_00_06_AvoidLossyConversion;
   }
}
