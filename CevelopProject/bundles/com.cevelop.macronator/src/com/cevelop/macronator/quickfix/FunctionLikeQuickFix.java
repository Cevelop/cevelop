package com.cevelop.macronator.quickfix;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.cevelop.macronator.MacronatorPlugin;
import com.cevelop.macronator.transform.AutoFunctionTransformer;
import com.cevelop.macronator.transform.MacroTransformation;
import com.cevelop.macronator.transform.VoidFunctionTransformer;


public class FunctionLikeQuickFix extends MacroQuickFix {

    @Override
    public String getLabel() {
        return "Replace macro definition with inline function";
    }

    @Override
    public void apply(final IASTPreprocessorMacroDefinition macroDefinition) {
        if (!(macroDefinition instanceof IASTPreprocessorFunctionStyleMacroDefinition)) {
            MacronatorPlugin.getDefault().getLog().log(new Status(IStatus.WARNING, MacronatorPlugin.PLUGIN_ID, "No macro definiton found"));
            return;
        }

        final IASTPreprocessorFunctionStyleMacroDefinition functionLikeMacro = (IASTPreprocessorFunctionStyleMacroDefinition) macroDefinition;
        final MacroTransformation macroTransformation = new MacroTransformation(new AutoFunctionTransformer(functionLikeMacro));
        if (macroTransformation.isValid()) {
            applyTransformation(macroDefinition, macroTransformation);
            return;
        }

        final MacroTransformation voidFunctionTransformation = new MacroTransformation(new VoidFunctionTransformer(functionLikeMacro));
        if (voidFunctionTransformation.isValid()) {
            applyTransformation(macroDefinition, voidFunctionTransformation);
        }
    }
}
