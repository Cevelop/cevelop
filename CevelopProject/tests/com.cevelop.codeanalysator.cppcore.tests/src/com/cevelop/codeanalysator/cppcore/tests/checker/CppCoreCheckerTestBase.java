package com.cevelop.codeanalysator.cppcore.tests.checker;

import com.cevelop.codeanalysator.core.tests.checker.CodeAnalysatorCheckerTestBase;
import com.cevelop.codeanalysator.cppcore.tests.util.CppCoreTestIdHelper;


public abstract class CppCoreCheckerTestBase extends CodeAnalysatorCheckerTestBase {

   @Override
   public Class<?> getPreferenceConstants() {
      return CppCoreTestIdHelper.class;
   }
}
