package com.cevelop.gslator.checkers.ES05ToES34DeclarationRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.ES05ToES34DeclarationRules.ES20AlwaysInitializeAnObjectVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class ES20AlwaysInitializeAnObjectChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Res-always";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new ES20AlwaysInitializeAnObjectVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.ES20;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_ES20;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
