package com.cevelop.codeanalysator.misra.tests.checker;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.misra.guideline.MisraGuideline;


public class BoolExpressionOperandsInfoCheckerTest extends MisraCheckerTestBase {

   @Override
   protected Rule getRuleToCheck() {
      return MisraGuideline.M04_05_01_BoolExpressionOperandsInfo;
   }
}
