package com.cevelop.gslator.checkers.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.C60ToC67CopyMoveRules.C66MoveOperationsShouldBeNoExceptVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class C66MoveOperationsShouldBeNoExceptChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-move-noexcept";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C66MoveOperationsShouldBeNoExceptVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.C66;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C66;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
