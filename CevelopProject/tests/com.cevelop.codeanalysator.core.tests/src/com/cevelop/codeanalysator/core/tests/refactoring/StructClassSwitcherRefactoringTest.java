package com.cevelop.codeanalysator.core.tests.refactoring;

import org.eclipse.ltk.core.refactoring.Refactoring;

import com.cevelop.codeanalysator.core.quickassist.refactoring.StructClassSwitcherRefactoring;


public class StructClassSwitcherRefactoringTest extends CodeAnalysatorRefactoringBaseTest {

   @Override
   public Refactoring createRefactoring() {
      return new StructClassSwitcherRefactoring(getPrimaryCElementFromCurrentProject().get(), getSelectionOfPrimaryTestFile());
   }
}
