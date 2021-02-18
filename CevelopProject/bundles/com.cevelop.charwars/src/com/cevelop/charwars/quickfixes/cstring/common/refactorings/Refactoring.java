package com.cevelop.charwars.quickfixes.cstring.common.refactorings;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.Context.Kind;


public abstract class Refactoring {

    protected Map<String, Object> config          = new HashMap<>();
    protected boolean             isApplicable;
    protected static final String NODE_TO_REPLACE = "NODE_TO_REPLACE";
    protected EnumSet<Kind>       contextKinds;

    public boolean tryToApply(IASTIdExpression idExpression, Context context, ASTChangeDescription changeDescription) {
        clearConfiguration();
        prepareConfiguration(idExpression, context);

        if (isApplicable(context.getKind())) {
            apply(idExpression, context);
            updateChangeDescription(changeDescription);
            return true;
        }
        return false;
    }

    protected void clearConfiguration() {
        config.clear();
        isApplicable = false;
    }

    protected abstract void prepareConfiguration(IASTIdExpression idExpression, Context context);

    protected void makeApplicable(IASTNode nodeToReplace) {
        isApplicable = true;
        config.put(NODE_TO_REPLACE, nodeToReplace);
    }

    protected boolean isApplicable(Kind kind) {
        return isApplicable && contextKinds.contains(kind);
    }

    protected void apply(IASTIdExpression idExpression, Context context) {
        IASTNode replacementNode = getReplacementNode(idExpression, context);
        if (replacementNode != null) {
            IASTNode nodeToReplace = (IASTNode) config.get(NODE_TO_REPLACE);
            ASTModifier.replaceNode(nodeToReplace, replacementNode);
        }
    }

    protected void updateChangeDescription(ASTChangeDescription changeDescription) {
        changeDescription.setStatementHasChanged(true);
    }

    protected IASTNode getReplacementNode(IASTIdExpression idExpression, Context context) {
        return null;
    }

    protected boolean canHandleOffsets() {
        return contextKinds.contains(Kind.Modified_String) || contextKinds.contains(Kind.Modified_Alias);
    }

    protected void setContextKinds(EnumSet<Kind> kinds) {
        contextKinds = kinds;
    }
}
