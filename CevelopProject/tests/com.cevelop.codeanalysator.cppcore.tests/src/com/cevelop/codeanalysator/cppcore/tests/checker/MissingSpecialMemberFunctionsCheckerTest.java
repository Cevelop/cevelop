package com.cevelop.codeanalysator.cppcore.tests.checker;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.cppcore.guideline.CppCoreGuideline;


public class MissingSpecialMemberFunctionsCheckerTest extends CppCoreCheckerTestBase {

   @Override
   protected Rule getRuleToCheck() {
      return CppCoreGuideline.C_021_MissingSpecialMemberFunctions;
   }
}
