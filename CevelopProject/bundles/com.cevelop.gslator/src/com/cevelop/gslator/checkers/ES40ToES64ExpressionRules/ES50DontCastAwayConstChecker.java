package com.cevelop.gslator.checkers.ES40ToES64ExpressionRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules.ES50DontCastAwayConstVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class ES50DontCastAwayConstChecker extends BaseChecker {

    public static final String IGNORE_STRING    = "Res-casts-const";
    public static final String PROFILE_GROUP    = "type";
    public static final String PROFILE_GROUP_NR = "3";

    @Override
    public void checkAst(IASTTranslationUnit ast) {
        ast.accept(new ES50DontCastAwayConstVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.ES50;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_ES50;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }

    @Override
    public String getProfileGroup() {
        return PROFILE_GROUP;
    }

    @Override
    public String getNrInProfileGroup() {
        return PROFILE_GROUP_NR;
    }
}
