package com.cevelop.codeanalysator.misra.tests.checker;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.misra.guideline.MisraGuideline;


public class BoolExpressionOperandsWarnCheckerTest extends MisraCheckerTestBase {

   @Override
   protected Rule getRuleToCheck() {
      return MisraGuideline.M04_05_01_BoolExpressionOperandsProblem;
   }
}
