package com.cevelop.gslator.checkers.C80ToC89OtherDefaultOperationRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.C80toC89OtherDefaultOperationRules.C83ValueLikeTypesShouldHaveSwapVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class C83ValueLikeTypesShouldHaveSwapChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-swap";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C83ValueLikeTypesShouldHaveSwapVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.C83;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C83;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
