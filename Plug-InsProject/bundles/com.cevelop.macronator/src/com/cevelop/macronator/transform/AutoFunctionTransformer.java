package com.cevelop.macronator.transform;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;


public class AutoFunctionTransformer extends ParameterizedExpressionTransformer {

    public AutoFunctionTransformer(final IASTPreprocessorFunctionStyleMacroDefinition macroDefinition) {
        super(macroDefinition);
    }

    @Override
    public String generateTransformationCode() {
        final StringBuilder transformation = new StringBuilder();
        transformation.append(generateTypenames(getFunctionStyleMacroDefinition().getParameters()));
        transformation.append("constexpr inline auto ");
        transformation.append(getFunctionStyleMacroDefinition().getName().toString());
        transformation.append(generateFunctionParameters(getFunctionStyleMacroDefinition().getParameters()));
        transformation.append(" -> decltype(" + getFunctionStyleMacroDefinition().getExpansion() + "){");
        transformation.append("return (");
        transformation.append(getFunctionStyleMacroDefinition().getExpansion());
        transformation.append("); }");
        return transformation.toString();
    }

}
