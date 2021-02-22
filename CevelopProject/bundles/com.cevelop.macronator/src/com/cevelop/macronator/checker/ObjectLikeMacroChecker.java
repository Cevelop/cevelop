package com.cevelop.macronator.checker;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;

import ch.hsr.ifs.iltis.cpp.core.wrappers.AbstractIndexAstChecker;

import com.cevelop.macronator.common.MacroClassifier;
import com.cevelop.macronator.common.MacroProperties;
import com.cevelop.macronator.transform.ConstexprTransformer;
import com.cevelop.macronator.transform.MacroTransformation;


public class ObjectLikeMacroChecker extends AbstractIndexAstChecker {

    @Override
    public void processAst(final IASTTranslationUnit ast) {
        final Map<ITranslationUnit, IASTTranslationUnit> astCache = new HashMap<>();
        for (final IASTPreprocessorMacroDefinition macro : ast.getMacroDefinitions()) {
            final MacroClassifier classifier = new MacroClassifier(macro, astCache);
            final MacroProperties properties = new MacroProperties(macro);
            if (classifier.isObjectLike() && classifier.areDependenciesValid() && new MacroTransformation(new ConstexprTransformer(macro))
                    .isValid() && !properties.suggestionsSuppressed()) {
                reportProblem(ProblemId.OBJECT_LIKE_MACRO, macro);
            }
        }

    }
}
