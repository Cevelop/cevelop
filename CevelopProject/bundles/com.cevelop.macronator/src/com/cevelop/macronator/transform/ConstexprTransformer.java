package com.cevelop.macronator.transform;

import org.eclipse.cdt.core.dom.ast.ExpansionOverlapsBoundaryException;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.parser.IToken;


public class ConstexprTransformer extends Transformer {

    private final IASTPreprocessorMacroDefinition macro;

    public ConstexprTransformer(final IASTPreprocessorMacroDefinition macro) {
        this.macro = macro;
    }

    @Override
    public String generateTransformationCode() {
        try {
            IToken token = macro.getSyntax().getNext().getNext(); // skip '#define'
            final StringBuilder replacementText = new StringBuilder(String.format("constexpr auto %s =", token.getImage()));
            token = token.getNext();
            while (token != null) {
                replacementText.append(token.getImage() + " ");
                token = token.getNext();
            }
            replacementText.append(";");
            return replacementText.toString();
        } catch (final ExpansionOverlapsBoundaryException e) {
            throw new RuntimeException(e);
        }
    }
}
