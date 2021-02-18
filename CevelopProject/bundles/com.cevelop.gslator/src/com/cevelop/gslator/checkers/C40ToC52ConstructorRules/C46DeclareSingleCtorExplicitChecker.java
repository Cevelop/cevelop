package com.cevelop.gslator.checkers.C40ToC52ConstructorRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.C40ToC52ConstructorRules.C46DeclareSingleCtorExplicitVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class C46DeclareSingleCtorExplicitChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-explicit";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C46DeclareSingleCtorExplicitVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.C46;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C46;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
