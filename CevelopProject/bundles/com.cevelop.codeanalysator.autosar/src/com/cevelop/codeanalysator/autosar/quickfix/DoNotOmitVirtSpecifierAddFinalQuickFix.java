package com.cevelop.codeanalysator.autosar.quickfix;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVirtSpecifier.SpecifierKind;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.codeanalysator.autosar.util.DeclaratorHelper;
import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;


public class DoNotOmitVirtSpecifierAddFinalQuickFix extends BaseQuickFix {

    public DoNotOmitVirtSpecifierAddFinalQuickFix(String label) {
        super(label);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (!(markedNode instanceof ICPPASTFunctionDeclarator)) {
            return;
        }
        ICPPASTFunctionDeclarator declarator = (ICPPASTFunctionDeclarator) markedNode;

        IASTDeclSpecifier declSpec = DeclaratorHelper.findDeclSpec(markedNode.getParent());
        IASTDeclSpecifier newDeclSpec = DeclaratorHelper.createDeclSpec(declSpec, factory);

        ICPPASTFunctionDeclarator newDecl = declarator.copy();
        newDecl.addVirtSpecifier(factory.newVirtSpecifier(SpecifierKind.Final));

        hRewrite.replace(declarator, newDecl, null);
        hRewrite.replace(declSpec, newDeclSpec, null);
        return;
    }
}
