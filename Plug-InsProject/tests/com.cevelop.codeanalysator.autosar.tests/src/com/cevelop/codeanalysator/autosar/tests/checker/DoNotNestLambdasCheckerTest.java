package com.cevelop.codeanalysator.autosar.tests.checker;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class DoNotNestLambdasCheckerTest extends AutosarCheckerTestBase {

   @Override
   protected Rule getRuleToCheck() {
      return AutosarGuideline.A05_01_08_DoNotNestLambdas;
   }
}
