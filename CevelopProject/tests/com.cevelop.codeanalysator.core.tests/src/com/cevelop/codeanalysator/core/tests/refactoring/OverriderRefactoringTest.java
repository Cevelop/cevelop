package com.cevelop.codeanalysator.core.tests.refactoring;

import org.eclipse.ltk.core.refactoring.Refactoring;

import com.cevelop.codeanalysator.core.quickassist.refactoring.OverriderRefactoring;


public class OverriderRefactoringTest extends CodeAnalysatorRefactoringBaseTest {

   @Override
   public Refactoring createRefactoring() {
      return new OverriderRefactoring(getPrimaryCElementFromCurrentProject().get(), getSelectionOfPrimaryTestFile());
   }
}
