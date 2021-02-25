package com.cevelop.codeanalysator.autosar.tests.quickfix;

import com.cevelop.codeanalysator.autosar.tests.util.AutosarTestIdHelper;
import com.cevelop.codeanalysator.core.tests.quickfix.CodeAnalysatorQuickFixTestBase;


public abstract class AutosarQuickFixTestBase extends CodeAnalysatorQuickFixTestBase {

   @Override
   public Class<?> getPreferenceConstants() {
      return AutosarTestIdHelper.class;
   }
}
