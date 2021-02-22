package com.cevelop.includator.helpers.offsetprovider;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;


public class PreprocessorInclude extends PreprocessorElement {

    private final IASTPreprocessorIncludeStatement include;

    public PreprocessorInclude(IASTPreprocessorIncludeStatement include, PreprocessorScope parent) {
        super(include, parent);
        this.include = include;
    }

    @Override
    public void accept(PreprocessorTreeVisitor visitor) {
        visitor.visit(this);
    }

    public boolean isSystemInclude() {
        return include.isSystemInclude();
    }
}
