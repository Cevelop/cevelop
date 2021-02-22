package com.cevelop.gslator.quickfixes.ES05ToES34DeclarationRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;


public class ES20AlwaysInitializeAnObjectQuickFix extends BaseQuickFix {

    @Override
    public String getLabel() {
        return Rule.ES20 + ": Replace with uniform variable initialization";
    }

    @SuppressWarnings("restriction")
    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {

        IASTDeclarator declaratorCopy = findDeclarator(markedNode).copy();

        declaratorCopy.setInitializer(factory.newInitializerList());

        astRewriteStore.getASTRewrite(markedNode).replace(markedNode, declaratorCopy, null);
    }

    private static IASTDeclarator findDeclarator(final IASTNode node) {
        if (node instanceof IASTDeclarator) {
            return (IASTDeclarator) node;
        }

        return findDeclarator(node.getParent());
    }
}
