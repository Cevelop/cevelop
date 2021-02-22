package com.cevelop.gslator.checkers.ES70toES86StatementRules;

import org.eclipse.cdt.codan.core.model.IProblemWorkingCopy;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.resources.IResource;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.ES70ToES86StatementRules.ES76AvoidGotoVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class ES76AvoidGotoChecker extends BaseChecker {

    public static final String IGNORE_STRING   = "Res-goto";
    public static final String PREF_MULTIBREAK = "enableMultibreakMarker";

    @Override
    public void checkAst(IASTTranslationUnit ast) {
        ast.accept(new ES76AvoidGotoVisitor(this));
    }

    @Override
    public void initPreferences(IProblemWorkingCopy problem) {
        super.initPreferences(problem);
        addPreference(problem, PREF_MULTIBREAK, "Mark goto's out of multiple loops", Boolean.TRUE);
    }

    @Override
    public Rule getRule() {
        return Rule.ES76;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_ES76;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }

    public boolean isMultibreakMarkerEnabled(IResource res) {
        return (boolean) getPreference(res, PREF_MULTIBREAK);
    }
}
