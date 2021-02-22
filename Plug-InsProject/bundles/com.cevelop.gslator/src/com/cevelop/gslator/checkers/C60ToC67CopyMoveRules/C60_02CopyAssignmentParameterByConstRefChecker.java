package com.cevelop.gslator.checkers.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.visitors.C60ToC67CopyMoveRules.C60_02CopyAssignmentParameterByConstRefVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;


public class C60_02CopyAssignmentParameterByConstRefChecker extends C60_00CopyAssignmentChecker {

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C60_02CopyAssignmentParameterByConstRefVisitor(this));
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C60_02;
    }
}
