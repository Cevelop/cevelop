package com.cevelop.gslator.checkers.C80ToC89OtherDefaultOperationRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.C80toC89OtherDefaultOperationRules.C84MakeSwapNoExceptVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class C84MakeSwapNoExceptChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-swap-fail";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C84MakeSwapNoExceptVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.C84;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C84;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
