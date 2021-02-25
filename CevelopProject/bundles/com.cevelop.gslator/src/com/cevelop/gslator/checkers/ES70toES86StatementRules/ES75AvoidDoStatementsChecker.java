package com.cevelop.gslator.checkers.ES70toES86StatementRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.ES70ToES86StatementRules.ES75AvoidDoStatementsVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class ES75AvoidDoStatementsChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Res-do";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new ES75AvoidDoStatementsVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.ES75;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_ES75;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
