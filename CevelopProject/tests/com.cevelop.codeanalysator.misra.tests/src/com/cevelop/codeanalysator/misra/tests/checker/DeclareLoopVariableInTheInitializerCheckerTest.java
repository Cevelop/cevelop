package com.cevelop.codeanalysator.misra.tests.checker;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.misra.guideline.MisraGuideline;


public class DeclareLoopVariableInTheInitializerCheckerTest extends MisraCheckerTestBase {

   @Override
   protected Rule getRuleToCheck() {
      return MisraGuideline.M03_04_01_DeclareLoopVariableInTheIntializer;
   }
}
