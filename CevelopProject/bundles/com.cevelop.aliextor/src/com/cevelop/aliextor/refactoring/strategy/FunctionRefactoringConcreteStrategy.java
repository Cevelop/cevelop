package com.cevelop.aliextor.refactoring.strategy;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.aliextor.ast.ASTHelper;
import com.cevelop.aliextor.ast.BaseASTVisitor;
import com.cevelop.aliextor.ast.selection.BasicRefactorSelection;
import com.cevelop.aliextor.refactoring.AliExtorRefactoring;


public class FunctionRefactoringConcreteStrategy extends SimpleRefactoringConcreteStrategy {

    private ICPPASTFunctionDefinition function  = null;
    private IASTParameterDeclaration  paramDecl = null;

    private boolean thisRefactoring = false;

    public FunctionRefactoringConcreteStrategy(AliExtorRefactoring proxy) {
        super(proxy);
    }

    @Override
    protected void init() {
        IASTNode node = refactorSelection.getSelectedNode();
        if (node instanceof ICPPASTFunctionDefinition) {
            function = (ICPPASTFunctionDefinition) refactorSelection.getSelectedNode();
            thisRefactoring = true;
        } else if (node instanceof ICPPASTParameterDeclaration) {
            IASTParameterDeclaration paramDecl = (IASTParameterDeclaration) refactorSelection.getSelectedNode();
            if (paramDecl.getDeclarator() instanceof ICPPASTFunctionDeclarator) {
                this.paramDecl = paramDecl;
                thisRefactoring = true;
            }
        }
        super.init();
    }

    @Override
    protected void checkSelection(RefactoringStatus status) {
        // Doesn't matter if nothing is found
        if (!thisRefactoring) {
            super.checkSelection(status);
        }
    }

    @Override
    protected BaseASTVisitor decideVisitor(IASTNode searchScope) {
        if (thisRefactoring) {
            return decideVisitor(searchScope, new BasicRefactorSelection(createParamDecl()));
        } else {
            return super.decideVisitor(searchScope);
        }
    }

    private IASTParameterDeclaration createParamDecl() {
        IASTParameterDeclaration paramDecl;
        if (function != null) {
            paramDecl = factory.newParameterDeclaration(function.getDeclSpecifier().copy(CopyStyle.withLocations), function.getDeclarator().copy(
                    CopyStyle.withLocations));
        } else {
            paramDecl = factory.newParameterDeclaration(this.paramDecl.getDeclSpecifier().copy(CopyStyle.withLocations), this.paramDecl
                    .getDeclarator().copy(CopyStyle.withLocations));
        }
        return paramDecl;
    }

    @Override
    protected void change(ASTRewrite rewrite, char[] aliasName) {
        if (thisRefactoring && !proxy.shouldJustRefactorSelected()) {
            assert (occurrences != null);
            for (IASTNode oldNode : occurrences) {
                doChange(rewrite, aliasName, oldNode, oldNode.copy());
            }
        } else {
            super.change(rewrite, aliasName);
        }
    }

    @Override
    protected void doChange(ASTRewrite rewrite, char[] aliasName, IASTNode oldNode, IASTNode newNode) {
        if (thisRefactoring) {
            rewrite.replace(oldNode, createReplacement(aliasName, newNode), null);
        } else {
            super.doChange(rewrite, aliasName, oldNode, newNode);
        }
    }

    private IASTNode createReplacement(char[] aliasName, IASTNode newNode) {
        ICPPASTNamedTypeSpecifier namedTypeSpecifier = factory.newTypedefNameSpecifier(factory.newName(aliasName));

        ICPPASTFunctionDeclarator newFuncDecl = (ICPPASTFunctionDeclarator) ((ICPPASTParameterDeclaration) newNode).getDeclarator();
        IASTDeclarator newNestedDecl = newFuncDecl.getNestedDeclarator();
        for (IASTPointerOperator pointer : newNestedDecl.getPointerOperators()) {
            newNestedDecl.addPointerOperator(pointer);
        }

        char newName[] = newFuncDecl.getNestedDeclarator().getName().toCharArray();

        IASTDeclarator newDeclarator = factory.newDeclarator(factory.newName(newName));

        IASTNode replacement = factory.newParameterDeclaration(namedTypeSpecifier, newDeclarator);
        return replacement;
    }

    @Override
    protected IASTNode createAliasStatement(char[] aliasName) {
        if (thisRefactoring) {
            IASTDeclSpecifier aliasDeclSpec = null;
            ICPPASTFunctionDeclarator aliasFuncDec = null;
            if (function != null) {
                aliasDeclSpec = function.getDeclSpecifier().copy(CopyStyle.withLocations);
                aliasFuncDec = (ICPPASTFunctionDeclarator) function.getDeclarator().copy(CopyStyle.withLocations);
                aliasFuncDec.setName(factory.newName());
            } else {
                aliasDeclSpec = paramDecl.getDeclSpecifier().copy(CopyStyle.withLocations);
                aliasFuncDec = factory.newFunctionDeclarator(factory.newName());
                for (IASTParameterDeclaration decl : ((ICPPASTFunctionDeclarator) paramDecl.getDeclarator()).getParameters()) {
                    aliasFuncDec.addParameterDeclaration(decl.copy(CopyStyle.withLocations));
                }
            }

            eraseParameterNames(aliasFuncDec);
            setRightExtractionType(aliasFuncDec);

            ICPPASTTypeId typeId = factory.newTypeId(aliasDeclSpec, aliasFuncDec);

            ICPPASTAliasDeclaration aliasDeclaration = factory.newAliasDeclaration(factory.newName(proxy.getTheUserInput().toCharArray()), typeId);
            IASTDeclarationStatement aliasDeclarationStatement = factory.newDeclarationStatement(aliasDeclaration);

            return aliasDeclarationStatement;
        }
        return super.createAliasStatement(aliasName);
    }

    private void setRightExtractionType(ICPPASTFunctionDeclarator aliasFuncDec) {
        if (!proxy.shouldExtractFunctionDeclaration()) {
            IASTDeclarator aliasDelcarator = factory.newDeclarator(factory.newName());
            if (proxy.shouldExtractFunctionPointer()) {
                aliasDelcarator.addPointerOperator(factory.newPointer());
            } else if (proxy.shouldExtractFunctionReference()) {
                aliasDelcarator.addPointerOperator(factory.newReferenceOperator(false));
            }
            aliasFuncDec.setNestedDeclarator(aliasDelcarator);
        }
    }

    private void eraseParameterNames(ICPPASTFunctionDeclarator aliasFuncDec) {
        for (ICPPASTParameterDeclaration paramDecl : aliasFuncDec.getParameters()) {
            paramDecl.getDeclarator().setName(factory.newName());
        }
    }

    @Override
    protected IASTNode getScope() {
        if (thisRefactoring) {
            return ASTHelper.findEnclosingScope(function != null ? function : paramDecl);
        } else {
            return super.getScope();
        }
    }

}
