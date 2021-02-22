package com.cevelop.aliextor.ast;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;

import com.cevelop.aliextor.ast.ASTHelper.Type;
import com.cevelop.aliextor.ast.selection.IRefactorSelection;


public class SimpleVisitor extends BaseASTVisitor {

    public SimpleVisitor(IRefactorSelection refactorSelection) {
        super(refactorSelection);
        shouldVisitDeclSpecifiers = true;
        shouldVisitDeclarations = true;
        shouldVisitTypeIds = true;
    }

    @Override
    public int visit(IASTDeclSpecifier declSpec) {
        // takes care off ICPPASTSimpleDeclSpecifier
        // ICPPASTNamedTypeSpecifier
        if (ASTHelper.isSameIASTDeclSpecifier(declSpec, refactorSelection.getSelectedNode())) {
            occurrences.add(declSpec);
        }
        return super.visit(declSpec);
    }

    @Override
    public int visit(IASTDeclaration declaration) {
        // takes care of ICPPASTSimpleDeclaration
        if (ASTHelper.hasSameCPPASTDeclaration(declaration, refactorSelection.getSelectedNode())) {
            occurrences.add(declaration);
        }
        return super.visit(declaration);
    }

    @Override
    public int visit(IASTTypeId typeId) {
        // takes care of IASTTypeId
        if (ASTHelper.isType(refactorSelection.getSelectedNode(), Type.ICPPASTTypeId) && ASTHelper.areSameTypeId(typeId,
                (IASTTypeId) refactorSelection.getSelectedNode())) {
            occurrences.add(typeId);
        }
        return super.visit(typeId);
    }

}
