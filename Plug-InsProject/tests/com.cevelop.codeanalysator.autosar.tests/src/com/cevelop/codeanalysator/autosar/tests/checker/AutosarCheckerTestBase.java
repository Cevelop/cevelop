package com.cevelop.codeanalysator.autosar.tests.checker;

import com.cevelop.codeanalysator.autosar.tests.util.AutosarTestIdHelper;
import com.cevelop.codeanalysator.core.tests.checker.CodeAnalysatorCheckerTestBase;


public abstract class AutosarCheckerTestBase extends CodeAnalysatorCheckerTestBase {

   @Override
   public Class<?> getPreferenceConstants() {
      return AutosarTestIdHelper.class;
   }
}
