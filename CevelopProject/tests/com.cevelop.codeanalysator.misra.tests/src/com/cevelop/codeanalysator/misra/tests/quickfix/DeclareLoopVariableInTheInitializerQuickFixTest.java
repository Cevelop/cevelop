package com.cevelop.codeanalysator.misra.tests.quickfix;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.quickfix.shared.DeclareLoopVariableInTheInitializerQuickFix;
import com.cevelop.codeanalysator.misra.guideline.MisraGuideline;


public class DeclareLoopVariableInTheInitializerQuickFixTest extends MisraQuickFixTestBase {

   @Override
   protected Rule getRuleToQuickFix() {
      return MisraGuideline.M03_04_01_DeclareLoopVariableInTheIntializer;
   }

   @Override
   protected IMarkerResolution getQuickFixToTest() {
      return new DeclareLoopVariableInTheInitializerQuickFix("");
   }
}
