package com.cevelop.includator.helpers.offsetprovider;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorObjectStyleMacroDefinition;


public class PreprocessorDefine extends PreprocessorElement {

    private final IASTPreprocessorObjectStyleMacroDefinition define;

    public PreprocessorDefine(IASTPreprocessorObjectStyleMacroDefinition define, PreprocessorScope parent) {
        super(define, parent);
        this.define = define;
    }

    @Override
    public void accept(PreprocessorTreeVisitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        IASTName name = define.getName();
        return name != null ? name.toString() : "";
    }
}
