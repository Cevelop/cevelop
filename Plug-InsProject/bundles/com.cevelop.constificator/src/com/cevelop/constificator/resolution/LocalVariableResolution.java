package com.cevelop.constificator.resolution;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;

import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.structural.Relation;


public class LocalVariableResolution implements Resolution<IASTSimpleDeclaration> {

    @Override
    public void handle(IASTNode node, IIndex index, ASTRewriteCache cache, IASTSimpleDeclaration ancestor) {
        ASTRewrite rewrite = cache.getASTRewrite(node.getTranslationUnit().getOriginatingTranslationUnit());

        if (rewrite == null) {
            return;
        }

        IASTNode copy = node.copy();

        if (node instanceof ICPPASTDeclSpecifier) {
            ((IASTDeclSpecifier) copy).setConst(true);
        } else if (node instanceof IASTPointer) {
            ((IASTPointer) copy).setConst(true);
        } else if (node instanceof ICPPASTName) {
            node = Relation.getAncestorOf(ICPPASTDeclSpecifier.class, node);
            if (node != null) {
                copy = node.copy();
                ((ICPPASTDeclSpecifier) copy).setConst(true);
            } else {
                return;
            }
        }

        rewrite.replace(node, copy, null);
    }

}
