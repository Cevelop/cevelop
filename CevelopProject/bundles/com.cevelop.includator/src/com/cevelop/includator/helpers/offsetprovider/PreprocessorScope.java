package com.cevelop.includator.helpers.offsetprovider;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.includator.helpers.FileHelper;


/**
 * Note that this scope representation is not fully correct. Even if containing scope-start and scope-end nodes, it cannot represent #else and #elfi
 * which should actually close the current scope and reopen a new one. Instead, currently #else and #elif are treated as a child which is incorrect,
 * but can be ignored when finding insertion-offsets. When required this should be adapted/fixed.
 */
public class PreprocessorScope extends BasePreprocessorElement {

    protected final int indentLevel;

    private final ArrayList<BasePreprocessorElement> containedPreprocessorStatements;

    private IASTNode scopeEndNode;

    public IASTNode getScopeEndNode() {
        return scopeEndNode;
    }

    private final IASTNode scopeStartNode;

    public PreprocessorScope() {
        super(null);
        indentLevel = 0;
        scopeStartNode = null;
        containedPreprocessorStatements = new ArrayList<>();
    }

    public PreprocessorScope(IASTNode scopeStartNode, PreprocessorScope parent) {
        super(parent);
        indentLevel = parent.indentLevel + 1;
        this.scopeStartNode = scopeStartNode;
        containedPreprocessorStatements = new ArrayList<>();
    }

    public void addChild(BasePreprocessorElement child) {
        containedPreprocessorStatements.add(child);
    }

    public void setScopeEndNode(IASTNode scopeEndNode) {
        this.scopeEndNode = scopeEndNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (scopeStartNode != null) {
            sb.append(indent(getRawSignature(scopeStartNode) + FileHelper.NL, (indentLevel - 1) * INDENT_WITH));
        }
        for (BasePreprocessorElement curChild : containedPreprocessorStatements) {
            sb.append(curChild.toString());
        }
        if (scopeEndNode != null) {
            sb.append(indent(getRawSignature(scopeEndNode) + FileHelper.NL, (indentLevel - 1) * INDENT_WITH));
        }
        return sb.toString();
    }

    @Override
    public void accept(PreprocessorTreeVisitor visitor) {
        visitor.visit(this);
        for (BasePreprocessorElement curChild : containedPreprocessorStatements) {
            curChild.accept(visitor);
        }
        visitor.leave(this);
    }

    public IASTNode getScopeStartNode() {
        return scopeStartNode;
    }

    public int getIndentLevel() {
        return indentLevel;
    }

    public ArrayList<BasePreprocessorElement> getContainedPreprocessorStatements() {
        return containedPreprocessorStatements;
    }
}
