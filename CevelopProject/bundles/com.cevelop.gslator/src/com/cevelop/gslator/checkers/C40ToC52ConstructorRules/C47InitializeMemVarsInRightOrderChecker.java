package com.cevelop.gslator.checkers.C40ToC52ConstructorRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.C40ToC52ConstructorRules.C47InitializeMemVarsInRightOrderVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class C47InitializeMemVarsInRightOrderChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-order";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C47InitializeMemVarsInRightOrderVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.C47;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C47;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
