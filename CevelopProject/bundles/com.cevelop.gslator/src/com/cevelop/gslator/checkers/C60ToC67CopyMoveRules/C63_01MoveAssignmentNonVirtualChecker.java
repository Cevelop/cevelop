package com.cevelop.gslator.checkers.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.visitors.C60ToC67CopyMoveRules.C63_01MoveAssignmentNonVirtualVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;


public class C63_01MoveAssignmentNonVirtualChecker extends C63_00MoveAssignmentChecker {

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C63_01MoveAssignmentNonVirtualVisitor(this));
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C63_01;
    }
}
