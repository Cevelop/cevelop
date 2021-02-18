package com.cevelop.macronator.checker;

import org.eclipse.cdt.codan.core.model.IProblemWorkingCopy;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.internal.core.model.ExternalTranslationUnit;

import com.cevelop.macronator.common.MacroProperties;

import ch.hsr.ifs.iltis.cpp.core.wrappers.AbstractIndexAstChecker;


public class UnusedMacroChecker extends AbstractIndexAstChecker {

    @Override
    public void processAst(final IASTTranslationUnit ast) {
        if (!(ast instanceof ExternalTranslationUnit)) {
            for (final IASTPreprocessorMacroDefinition macro : ast.getMacroDefinitions()) {
                final MacroProperties properties = new MacroProperties(macro);
                if (isNeverUsed(macro) && macro.isActive() && !properties.suggestionsSuppressed()) {
                    reportProblem(ProblemId.UNUSED_MACRO, macro);
                }
            }
        }
    }

    @Override
    public void initPreferences(final IProblemWorkingCopy problem) {
        super.initPreferences(problem);
        getLaunchModePreference(problem).enableInLaunchModes(); // disable by default
    }

    private boolean isNeverUsed(final IASTPreprocessorMacroDefinition macro) {
        return new MacroProperties(macro).getReferences().length == 0;
    }
}
