package com.cevelop.gslator.checkers.C160ToC170OverloadingRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.C160ToC170OverloadingRules.C164AvoidConversionOperatorsVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class C164AvoidConversionOperatorsChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Ro-conversion";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C164AvoidConversionOperatorsVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.C164;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C164;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
