package com.cevelop.gslator.checkers.ES40ToES64ExpressionRules;

import org.eclipse.cdt.codan.core.model.IProblemWorkingCopy;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules.ES49IfMustUseNamedCastVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class ES49IfMustUseNamedCastChecker extends BaseChecker {

    public static final String IGNORE_STRING    = "Res-casts-named";
    public static final String PROFILE_GROUP    = "type";
    public static final String PROFILE_GROUP_NR = "4";

    public static final String PREF_ENABLE_TRADITIONALCAST          = "enableTraditionalCast";
    public static final String PREF_ENABLE_TYPECONST_WITH_CONSTINIT = "enableTypeconstWithConstInit";
    public static final String PREF_ENABLE_REINTERPRET_QUICKFIX     = "enableReinterpretQuickFix";

    @Override
    public void checkAst(IASTTranslationUnit ast) {
        ast.accept(new ES49IfMustUseNamedCastVisitor(this));
    }

    @Override
    public void initPreferences(IProblemWorkingCopy problem) {
        super.initPreferences(problem);
        addPreference(problem, PREF_ENABLE_TRADITIONALCAST, "Mark traditional C-style casts", Boolean.TRUE);
        addPreference(problem, PREF_ENABLE_TYPECONST_WITH_CONSTINIT, "Mark functional casts", Boolean.TRUE);
        addPreference(problem, PREF_ENABLE_REINTERPRET_QUICKFIX, "Enable \"Use reinterpret_cast\" quickfix", Boolean.FALSE);
    }

    @Override
    public Rule getRule() {
        return Rule.ES49;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_ES49;
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
