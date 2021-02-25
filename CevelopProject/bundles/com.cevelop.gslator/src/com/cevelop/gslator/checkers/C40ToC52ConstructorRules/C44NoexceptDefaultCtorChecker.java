package com.cevelop.gslator.checkers.C40ToC52ConstructorRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.C40ToC52ConstructorRules.C44NoexceptDefaultCtorVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class C44NoexceptDefaultCtorChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-default00";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C44NoexceptDefaultCtorVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.C44;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C44;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
