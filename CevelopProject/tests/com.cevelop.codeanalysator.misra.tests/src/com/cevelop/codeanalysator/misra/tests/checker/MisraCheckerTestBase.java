package com.cevelop.codeanalysator.misra.tests.checker;

import com.cevelop.codeanalysator.core.tests.checker.CodeAnalysatorCheckerTestBase;
import com.cevelop.codeanalysator.misra.tests.util.MisraTestIdHelper;


public abstract class MisraCheckerTestBase extends CodeAnalysatorCheckerTestBase {

   @Override
   public Class<?> getPreferenceConstants() {
      return MisraTestIdHelper.class;
   }
}
