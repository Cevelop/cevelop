package com.cevelop.codeanalysator.cppcore.tests.checker;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.cppcore.guideline.CppCoreGuideline;


public abstract class AvoidLossyConversionCheckerTest extends CppCoreCheckerTestBase {

   @Override
   protected Rule getRuleToCheck() {
      return CppCoreGuideline.ES_046_AvoidLossyConversions;
   }
}
