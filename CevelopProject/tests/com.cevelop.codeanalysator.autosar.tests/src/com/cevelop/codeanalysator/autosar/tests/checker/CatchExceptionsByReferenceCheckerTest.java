package com.cevelop.codeanalysator.autosar.tests.checker;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.core.guideline.Rule;

public class CatchExceptionsByReferenceCheckerTest extends AutosarCheckerTestBase{

   @Override
   protected Rule getRuleToCheck() {
      return AutosarGuideline.A15_03_05_CatchExceptionsByReference;
   }

}
