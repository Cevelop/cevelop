package com.cevelop.namespactor.tests.bugs

import com.cevelop.namespactor.refactoring.itda.ITDARefactoring
import com.cevelop.namespactor.tests.NamespactorTest
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring

class InlineTypeAliasRefactoringBugs : NamespactorTest() {
	
   override fun getRefactoring(): CRefactoring {
      return ITDARefactoring(getPrimaryCElementFromCurrentProject().get(), getSelectionOfPrimaryTestFile());
   }

   protected override fun initAdditionalIncludes() {
      stageExternalIncludePathsForBothProjects("include");
      super.initAdditionalIncludes();
   }
}