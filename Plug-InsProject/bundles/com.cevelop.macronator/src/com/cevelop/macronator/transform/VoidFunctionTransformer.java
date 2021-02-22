package com.cevelop.macronator.transform;

import org.eclipse.cdt.core.dom.ast.IASTFunctionStyleMacroParameter;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;


public class VoidFunctionTransformer extends ParameterizedExpressionTransformer {

    public VoidFunctionTransformer(final IASTPreprocessorFunctionStyleMacroDefinition macro) {
        super(macro);
    }

    @Override
    public String generateTransformationCode() {
        final StringBuilder transformation = new StringBuilder();
        IASTFunctionStyleMacroParameter[] parameters = getFunctionStyleMacroDefinition().getParameters();
        String expansion = getFunctionStyleMacroDefinition().getExpansion();
        transformation.append(generateTypenames(parameters));
        if (parameters.length == 0) {
            transformation.append(" inline");
        }
        transformation.append(" void ");
        transformation.append(getFunctionStyleMacroDefinition().getName().toString());
        transformation.append(generateFunctionParameters(parameters));
        transformation.append("{");
        transformation.append(expansion);
        if (!expansion.endsWith(";") && !expansion.endsWith("}")) {
            transformation.append(";");
        }
        return transformation.append("}").toString();
    }
}
