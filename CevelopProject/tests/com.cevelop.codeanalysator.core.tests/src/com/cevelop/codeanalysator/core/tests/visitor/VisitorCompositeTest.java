package com.cevelop.codeanalysator.core.tests.visitor;

import static org.junit.Assert.assertEquals;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.junit.Test;

import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.VisitorComposite;


@SuppressWarnings("restriction")
public class VisitorCompositeTest {
   
   abstract class MockVisitor extends CodeAnalysatorVisitor {
      protected MockVisitor() {
         super(null, null);
      }

      @Override
      protected void setShouldVisit() {
         this.shouldVisitDeclarators = true;
      }
   }

   class AbortVisitor extends MockVisitor {
      @Override
      public int visit(IASTDeclarator declarator) {
         return PROCESS_ABORT;
      }
   }

   class SkipVisitor extends MockVisitor {
      @Override
      public int visit(IASTDeclarator declarator) {
         return PROCESS_SKIP;
      }
   }

   class ContinueVisitor extends MockVisitor {
      @Override
      public int visit(IASTDeclarator declarator) {
         return PROCESS_CONTINUE;
      }
   }
   
   @Test()
   public void testShouldAbortIfAllVisitorsAborted() {
      VisitorComposite visitor = new VisitorComposite();
      visitor.add(new AbortVisitor());
      visitor.add(new AbortVisitor());

      assertEquals(ASTVisitor.PROCESS_ABORT, visitor.visit(new CPPASTDeclarator()));
   }

   @Test()
   public void testShouldNotAbortIfNotAllVisitorsAborted() {
      VisitorComposite visitor = new VisitorComposite();
      visitor.add(new AbortVisitor());
      visitor.add(new ContinueVisitor());

      assertEquals(ASTVisitor.PROCESS_CONTINUE, visitor.visit(new CPPASTDeclarator()));
   }

   @Test()
   public void testShouldContinueIfNotAllSkipped() {
      VisitorComposite visitor = new VisitorComposite();
      visitor.add(new SkipVisitor());
      visitor.add(new ContinueVisitor());

      assertEquals(ASTVisitor.PROCESS_CONTINUE, visitor.visit(new CPPASTDeclarator()));
   }

   @Test()
   public void testShouldSkipIfAllSkipped() {
      VisitorComposite visitor = new VisitorComposite();
      visitor.add(new SkipVisitor());
      visitor.add(new SkipVisitor());

      assertEquals(ASTVisitor.PROCESS_SKIP, visitor.visit(new CPPASTDeclarator()));
      assertEquals(0, visitor.getVisitors().size());
   }

   @Test()
   public void testShouldReturnSkippedVisitorsOnLeave() {
      VisitorComposite visitor = new VisitorComposite();
      visitor.add(new SkipVisitor());
      visitor.add(new SkipVisitor());

      IASTDeclarator node = new CPPASTDeclarator();
      assertEquals(ASTVisitor.PROCESS_SKIP, visitor.visit(node));
      assertEquals(0, visitor.getVisitors().size());
      assertEquals(ASTVisitor.PROCESS_CONTINUE, visitor.leave(node));
      assertEquals(2, visitor.getVisitors().size());
   }

   @Test()
   public void testShouldNotAbortIfSkippedVisitorsExists() {
      VisitorComposite visitor = new VisitorComposite();
      visitor.add(new SkipVisitor());
      visitor.add(new AbortVisitor());

      IASTDeclarator node = new CPPASTDeclarator();
      assertEquals(ASTVisitor.PROCESS_SKIP, visitor.visit(node));
      assertEquals(0, visitor.getVisitors().size());
      assertEquals(ASTVisitor.PROCESS_CONTINUE, visitor.leave(node));
      assertEquals(1, visitor.getVisitors().size());
   }
}
