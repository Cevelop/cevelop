package com.cevelop.codeanalysator.cppcore.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.quickfix.shared.DeclareLoopVariableInTheInitializerQuickFix;
import com.cevelop.codeanalysator.cppcore.guideline.CppCoreGuideline;


public class DeclareLoopVariableInTheInitializerQuickFixTest extends CppCoreQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return CppCoreGuideline.ES_074_DeclareLoopVariableInTheIntializer;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new DeclareLoopVariableInTheInitializerQuickFix("");
   }
}
