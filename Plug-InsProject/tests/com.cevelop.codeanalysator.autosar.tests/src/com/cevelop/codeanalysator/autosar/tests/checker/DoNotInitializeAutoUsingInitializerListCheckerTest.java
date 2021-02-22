package com.cevelop.codeanalysator.autosar.tests.checker;

import com.cevelop.codeanalysator.autosar.guideline.AutosarGuideline;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class DoNotInitializeAutoUsingInitializerListCheckerTest extends AutosarCheckerTestBase {

   @Override
   protected Rule getRuleToCheck() {
      return AutosarGuideline.A08_05_03_DoNotInitializeAutoUsingInitializerList;
   }
}
