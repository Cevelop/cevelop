package com.cevelop.gslator.checkers.C40ToC52ConstructorRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.C40ToC52ConstructorRules.C48PreferInClassInitializerToCtorsVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class C48PreferInClassInitializerToCtorInitChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-in-class-initializer";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C48PreferInClassInitializerToCtorsVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.C48;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C48;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
