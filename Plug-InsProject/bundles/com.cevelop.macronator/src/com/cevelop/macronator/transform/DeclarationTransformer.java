package com.cevelop.macronator.transform;

import org.eclipse.cdt.core.dom.ast.IASTFunctionStyleMacroParameter;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;


public class DeclarationTransformer extends ParameterizedExpressionTransformer {

    public DeclarationTransformer(final IASTPreprocessorFunctionStyleMacroDefinition macro) {
        super(macro);
    }

    @Override
    public String generateTransformationCode() {
        final String type = getFunctionStyleMacroDefinition().getParameters()[0].getParameter();
        final String name = getFunctionStyleMacroDefinition().getParameters()[1].getParameter();
        return String.format("%s %s;", type, name);
    }

    private boolean endsWithSemicolon() {
        return getFunctionStyleMacroDefinition().getExpansion().endsWith(";");
    }

    @Override
    public boolean isValid() {
        final IASTFunctionStyleMacroParameter[] parameters = getFunctionStyleMacroDefinition().getParameters();
        return (parameters != null && parameters.length == 2 && endsWithSemicolon());
    }
}
