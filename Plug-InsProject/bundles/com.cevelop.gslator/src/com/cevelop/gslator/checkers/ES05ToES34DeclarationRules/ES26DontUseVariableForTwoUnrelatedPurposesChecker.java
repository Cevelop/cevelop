package com.cevelop.gslator.checkers.ES05ToES34DeclarationRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.ES05ToES34DeclarationRules.ES26DontUseVariableForTwoUnrelatedPurposesVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class ES26DontUseVariableForTwoUnrelatedPurposesChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Res-recycle";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new ES26DontUseVariableForTwoUnrelatedPurposesVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.ES26;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_ES26;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
