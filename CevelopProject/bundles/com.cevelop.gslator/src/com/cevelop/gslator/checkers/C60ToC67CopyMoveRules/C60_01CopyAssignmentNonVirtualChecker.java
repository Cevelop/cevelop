package com.cevelop.gslator.checkers.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.visitors.C60ToC67CopyMoveRules.C60_01CopyAssignmentNonVirtualVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;


public class C60_01CopyAssignmentNonVirtualChecker extends C60_00CopyAssignmentChecker {

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C60_01CopyAssignmentNonVirtualVisitor(this));
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C60_01;
    }
}
