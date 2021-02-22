package com.cevelop.includator.helpers.offsetprovider;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.includator.helpers.FileHelper;


public class PreprocessorElement extends BasePreprocessorElement {

    private final IASTNode enclosedNode;

    public PreprocessorElement(IASTNode element, PreprocessorScope parent) {
        super(parent);
        enclosedNode = element;
    }

    @Override
    public String toString() {
        return indent(enclosedNode.getRawSignature() + FileHelper.NL, getParent().indentLevel * INDENT_WITH);
    }

    @Override
    public void accept(PreprocessorTreeVisitor visitor) {
        visitor.visit(this);
    }

    public IASTNode getEnclosedNode() {
        return enclosedNode;
    }
}
