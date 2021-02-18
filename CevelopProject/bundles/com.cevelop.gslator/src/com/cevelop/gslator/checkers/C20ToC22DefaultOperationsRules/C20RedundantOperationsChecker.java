package com.cevelop.gslator.checkers.C20ToC22DefaultOperationsRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.C20ToC22DefaultOperationsRules.C20RedundantOperationsVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class C20RedundantOperationsChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-zero";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C20RedundantOperationsVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.C20;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C20;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
