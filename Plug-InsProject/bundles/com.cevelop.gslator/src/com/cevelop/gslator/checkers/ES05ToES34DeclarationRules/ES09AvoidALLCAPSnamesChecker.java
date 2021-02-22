package com.cevelop.gslator.checkers.ES05ToES34DeclarationRules;

import org.eclipse.cdt.codan.core.model.IProblemWorkingCopy;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.ES05ToES34DeclarationRules.ES09AvoidALLCAPSnamesVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class ES09AvoidALLCAPSnamesChecker extends BaseChecker {

    public static final String IGNORE_STRING    = "Res-not-CAPS";
    public static final String PREF_MARK_MACROS = "markMacros";

    @Override
    public void checkAst(IASTTranslationUnit ast) {
        ast.accept(new ES09AvoidALLCAPSnamesVisitor(this));
    }

    @Override
    public void initPreferences(IProblemWorkingCopy problem) {
        super.initPreferences(problem);
        addPreference(problem, PREF_MARK_MACROS, "Mark non-ALL_CAPS Makros", Boolean.FALSE);
    }

    @Override
    public Rule getRule() {
        return Rule.ES09;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_ES09;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }

    @Override
    public boolean isIgnoreApplicable(IMarker marker, IASTNode iastNode) {
        return !(iastNode instanceof IASTPreprocessorMacroDefinition);
    }
}
