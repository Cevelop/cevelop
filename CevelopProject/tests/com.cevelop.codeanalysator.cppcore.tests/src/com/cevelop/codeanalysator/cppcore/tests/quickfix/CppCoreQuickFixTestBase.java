package com.cevelop.codeanalysator.cppcore.tests.quickfix;

import com.cevelop.codeanalysator.core.tests.quickfix.CodeAnalysatorQuickFixTestBase;
import com.cevelop.codeanalysator.cppcore.tests.util.CppCoreTestIdHelper;


public abstract class CppCoreQuickFixTestBase extends CodeAnalysatorQuickFixTestBase {

   @Override
   public Class<?> getPreferenceConstants() {
      return CppCoreTestIdHelper.class;
   }
}
