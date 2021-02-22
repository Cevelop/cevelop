package com.cevelop.macronator.transform;

import org.eclipse.cdt.core.dom.ast.IASTFunctionStyleMacroParameter;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;


public abstract class ParameterizedExpressionTransformer extends Transformer {

    private final String                                       PARAMETER_PATTERN = "T%s&& %s";
    private final IASTPreprocessorFunctionStyleMacroDefinition macro;

    public ParameterizedExpressionTransformer(final IASTPreprocessorFunctionStyleMacroDefinition macro) {
        this.macro = macro;
    }

    protected String generateFunctionParameters(final IASTFunctionStyleMacroParameter[] parameters) {
        final StringBuilder fParameters = new StringBuilder("(");
        if (parameters.length > 0) {
            fParameters.append(String.format(PARAMETER_PATTERN, 1, parameters[0].getParameter()));
            for (int i = 1; i < parameters.length; i++) {
                fParameters.append(',').append(String.format(PARAMETER_PATTERN, (i + 1), parameters[i].getParameter()));
            }
        }
        return fParameters.append(")").toString();
    }

    protected String generateTypenames(final IASTFunctionStyleMacroParameter[] parameters) {
        if (parameters.length > 0) {
            final StringBuilder typenames = new StringBuilder("template <");
            typenames.append("typename T1");
            for (int i = 1; i < parameters.length; i++) {
                typenames.append(", typename T" + (i + 1));
            }
            return typenames.append(">").toString();
        } else {
            return "";
        }
    }

    protected IASTPreprocessorFunctionStyleMacroDefinition getFunctionStyleMacroDefinition() {
        return macro;
    }
}
