package com.cevelop.gslator.checkers.C30ToC37DestructorRules;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.C30ToC37DestructorRules.C35BaseClassDestructorVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class C35BaseClassDestructorChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-dtor-virtual";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C35BaseClassDestructorVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.C35;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C35;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }
}
