package com.cevelop.codeanalysator.autosar.tests.checker;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class InitializeNoneFirstOrAllEnumeratorsCheckerTest extends AutosarCheckerTestBase {

   @Override
   protected Rule getRuleToCheck() {
      return AutosarGuideline.A07_02_04_InitializeNoneFirstOrAll;
   }
}
