package com.cevelop.gslator.checkers.C80ToC89OtherDefaultOperationRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.C80toC89OtherDefaultOperationRules.C85NamespaceLevelSwapFunctionVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class C85NamespaceLevelSwapFunctionChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-swap-noexcept";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C85NamespaceLevelSwapFunctionVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.C85;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C85;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
