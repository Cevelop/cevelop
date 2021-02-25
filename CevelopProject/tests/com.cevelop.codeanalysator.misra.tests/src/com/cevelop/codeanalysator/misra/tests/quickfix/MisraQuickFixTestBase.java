package com.cevelop.codeanalysator.misra.tests.quickfix;

import com.cevelop.codeanalysator.core.tests.quickfix.CodeAnalysatorQuickFixTestBase;
import com.cevelop.codeanalysator.misra.tests.util.MisraTestIdHelper;


public abstract class MisraQuickFixTestBase extends CodeAnalysatorQuickFixTestBase {

   @Override
   public Class<?> getPreferenceConstants() {
      return MisraTestIdHelper.class;
   }
}
