package com.cevelop.macronator.checker;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;

import ch.hsr.ifs.iltis.cpp.core.wrappers.AbstractIndexAstChecker;

import com.cevelop.macronator.common.MacroClassifier;
import com.cevelop.macronator.common.MacroProperties;
import com.cevelop.macronator.transform.AutoFunctionTransformer;
import com.cevelop.macronator.transform.DeclarationTransformer;
import com.cevelop.macronator.transform.MacroTransformation;
import com.cevelop.macronator.transform.VoidFunctionTransformer;


public class FunctionLikeMacroChecker extends AbstractIndexAstChecker {

    @Override
    public void processAst(final IASTTranslationUnit ast) {
        final Map<ITranslationUnit, IASTTranslationUnit> astCache = new HashMap<>();
        astCache.put(ast.getOriginatingTranslationUnit(), ast);
        for (final IASTPreprocessorMacroDefinition macro : ast.getMacroDefinitions()) {
            final MacroClassifier classifier = new MacroClassifier(macro, astCache);
            final MacroProperties properties = new MacroProperties(macro);
            if (classifier.isFunctionLike() && isTransformationValid((IASTPreprocessorFunctionStyleMacroDefinition) macro) && classifier
                    .areDependenciesValid() && !properties.suggestionsSuppressed()) {
                reportProblem(ProblemId.FUN_LIKE_MACRO, macro);
            }
        }
    }

    private boolean isTransformationValid(final IASTPreprocessorFunctionStyleMacroDefinition macro) {
        return isTransformableToFunction(macro) && !new DeclarationTransformer(macro).isValid();
    }

    private boolean isTransformableToFunction(final IASTPreprocessorFunctionStyleMacroDefinition macro) {
        return new MacroTransformation(new AutoFunctionTransformer(macro)).isValid() || new MacroTransformation(new VoidFunctionTransformer(macro))
                .isValid();
    }
}
