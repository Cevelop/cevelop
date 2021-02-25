package com.cevelop.codeanalysator.autosar.quickfix;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;


public class DoNotUseRegisterQuickFix extends BaseQuickFix {

    public DoNotUseRegisterQuickFix(String label) {
        super(label);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (markedNode instanceof IASTSimpleDeclaration) {
            removeRegisterInDeclaration(markedNode, hRewrite);
        } else if (markedNode instanceof IASTParameterDeclaration) {
            removeRegisterInParameter(markedNode, hRewrite);
        }
    }

    private void removeRegisterInParameter(IASTNode markedNode, ASTRewrite hRewrite) {
        IASTParameterDeclaration decl = (IASTParameterDeclaration) markedNode.copy();
        IASTDeclSpecifier declSpec = decl.getDeclSpecifier();
        declSpec.setStorageClass(IASTDeclSpecifier.sc_unspecified);
        hRewrite.replace(markedNode, decl, null);
    }

    private void removeRegisterInDeclaration(IASTNode markedNode, ASTRewrite hRewrite) {
        IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) markedNode.copy();
        IASTDeclSpecifier declSpec = simpleDecl.getDeclSpecifier();
        declSpec.setStorageClass(IASTDeclSpecifier.sc_unspecified);
        hRewrite.replace(markedNode, simpleDecl, null);
    }
}
