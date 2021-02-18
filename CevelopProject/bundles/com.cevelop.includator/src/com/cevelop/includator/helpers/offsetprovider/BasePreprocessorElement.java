package com.cevelop.includator.helpers.offsetprovider;

import org.eclipse.cdt.core.dom.ast.IASTNode;


public abstract class BasePreprocessorElement {

    public static final int INDENT_WITH = 4;

    PreprocessorScope parent;

    public BasePreprocessorElement(PreprocessorScope parent) {
        this.parent = parent;
    }

    public PreprocessorScope getParent() {
        return parent;
    }

    public void setParent(PreprocessorScope parent) {
        this.parent = parent;
    }

    protected static String indent(String s, int n) {
        return String.format("%1$#" + (n + s.length()) + "s", s);
    }

    protected static String getRawSignature(IASTNode node) {
        return (node != null) ? node.getRawSignature() : "";
    }

    public abstract void accept(PreprocessorTreeVisitor visitor);
}
