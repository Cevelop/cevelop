package com.cevelop.gslator.checkers.C30ToC37DestructorRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.C30ToC37DestructorRules.C37DestructorShouldBeNoExceptVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class C37DestructorShouldBeNoExceptChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-dtor-noexcept";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C37DestructorShouldBeNoExceptVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.C37;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C37;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
