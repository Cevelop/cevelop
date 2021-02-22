package com.cevelop.macronator.quickfix;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;

import com.cevelop.macronator.transform.MacroTransformation;
import com.cevelop.macronator.transform.Transformer;


public class UnusedMacroQuickfix extends MacroQuickFix {

    @Override
    public String getLabel() {
        return "remove macro";
    }

    @Override
    public void apply(final IASTPreprocessorMacroDefinition macroDefinition) {
        this.applyTransformation(macroDefinition, new MacroTransformation(new Transformer() {

            @Override
            public String generateTransformationCode() {
                return "";
            }
        }));
    }
}
