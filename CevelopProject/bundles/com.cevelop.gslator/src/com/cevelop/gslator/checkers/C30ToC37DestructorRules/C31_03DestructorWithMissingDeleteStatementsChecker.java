package com.cevelop.gslator.checkers.C30ToC37DestructorRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.visitors.C30ToC37DestructorRules.C31_03DestructorWithMissingDeleteStatementsVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;


public class C31_03DestructorWithMissingDeleteStatementsChecker extends C31_00DeleteOwnersInDestructorChecker {

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C31_03DestructorWithMissingDeleteStatementsVisitor(this));
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C31_03;
    }
}
