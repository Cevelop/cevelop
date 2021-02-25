package com.cevelop.codeanalysator.core.tests.refactoring;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.junit.Test;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingRefactoringTest;


public abstract class CodeAnalysatorRefactoringBaseTest extends CDTTestingRefactoringTest {

   protected Refactoring refactoring;

   @Override
   protected abstract Refactoring createRefactoring();

   @Test
   public void runTest() throws Throwable {

      refactoring = createRefactoring();
      refactoring.checkInitialConditions(new NullProgressMonitor());
      refactoring.checkFinalConditions(new NullProgressMonitor());
      Change change = refactoring.createChange(new NullProgressMonitor());

      change.perform(new NullProgressMonitor());
      assertAllSourceFilesEqual(COMPARE_AST_AND_COMMENTS_AND_INCLUDES);
   }
}
