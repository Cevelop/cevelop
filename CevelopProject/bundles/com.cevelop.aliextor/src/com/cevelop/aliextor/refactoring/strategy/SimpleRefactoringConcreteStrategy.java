package com.cevelop.aliextor.refactoring.strategy;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.aliextor.ast.ASTHelper;
import com.cevelop.aliextor.ast.ASTHelper.Type;
import com.cevelop.aliextor.ast.BaseASTVisitor;
import com.cevelop.aliextor.ast.FunctionParameterVisitor;
import com.cevelop.aliextor.ast.SimpleVisitor;
import com.cevelop.aliextor.ast.selection.IRefactorSelection;
import com.cevelop.aliextor.refactoring.AliExtorRefactoring;


public class SimpleRefactoringConcreteStrategy extends RefactoringStrategy {

    public SimpleRefactoringConcreteStrategy(AliExtorRefactoring proxy) {
        super(proxy);
    }

    @Override
    public RefactoringStatus checkTheInitialConditions(RefactoringStatus status) {
        checkSelection(status);

        return status;
    }

    @Override
    protected BaseASTVisitor decideVisitor(IASTNode searchScope) {
        return decideVisitor(searchScope, refactorSelection);
    }

    protected BaseASTVisitor decideVisitor(IASTNode searchScope, IRefactorSelection selection) {
        if (ASTHelper.isType(searchScope, Type.ICPPASTTranslationUnit)) {
            return new FunctionParameterVisitor(selection);
        } else if (ASTHelper.isType(searchScope, Type.ICPPASTCompoundStatement)) {
            return new SimpleVisitor(selection);
        }
        return null;
    }

    @Override
    public void collectModifications(ASTRewrite rewrite) {
        char[] aliasName = proxy.getTheUserInput().toCharArray();

        IASTNode aliasStatement = createAliasStatement(aliasName);

        change(rewrite, aliasName);

        placeAliasDeclaration(rewrite, refactorSelection.getSelectedNode(), aliasStatement);
    }

    protected void change(ASTRewrite rewrite, char[] aliasName) {
        if (proxy.shouldJustRefactorSelected()) {
            IASTNode newNode = refactorSelection.getSelectedNode().copy();
            doChange(rewrite, aliasName, refactorSelection.getSelectedNode(), newNode);
        } else {
            assert (occurrences != null);
            for (IASTNode oldNode : occurrences) {
                doChange(rewrite, aliasName, oldNode, oldNode.copy());
            }
        }
    }

    protected void doChange(ASTRewrite rewrite, char[] aliasName, IASTNode oldNode, IASTNode newNode) {
        if (ASTHelper.isType(oldNode, Type.IASTDeclSpecifier)) {
            doDeclSpecifierChange(rewrite, aliasName, oldNode);
        } else if (ASTHelper.isType(oldNode, Type.IASTSimpleDeclaration)) {
            doDeclarationChange(rewrite, aliasName, oldNode, newNode);
        } else if (ASTHelper.isType(oldNode, Type.ICPPASTTypeId)) {
            doTypeIdChange(rewrite, aliasName, oldNode);
        }
    }

    protected void doDeclSpecifierChange(ASTRewrite rewrite, char[] aliasName, IASTNode oldNode) {
        ICPPASTDeclSpecifier replacement = factory.newTypedefNameSpecifier(factory.newName(aliasName));
        replacement.setStorageClass(((ICPPASTDeclSpecifier) oldNode).getStorageClass());

        rewrite.replace(oldNode, replacement, null);
    }

    protected void doDeclarationChange(ASTRewrite rewrite, char[] aliasName, IASTNode oldNode, IASTNode newNode) {
        ICPPASTNamedTypeSpecifier namedTypeSpecifier = factory.newTypedefNameSpecifier(factory.newName(aliasName));
        namedTypeSpecifier.setStorageClass(((IASTSimpleDeclaration) oldNode).getDeclSpecifier().getStorageClass());
        IASTSimpleDeclaration declaration = factory.newSimpleDeclaration(namedTypeSpecifier);

        ICPPASTDeclarator declarator = null;

        if (((IASTSimpleDeclaration) oldNode).getDeclarators()[0] instanceof IASTArrayDeclarator) {
            declarator = factory.newArrayDeclarator(((IASTSimpleDeclaration) newNode).getDeclarators()[0].getName());

            for (IASTArrayModifier arrayMod : ((IASTArrayDeclarator) ((IASTSimpleDeclaration) newNode).getDeclarators()[0]).getArrayModifiers()) {
                ((ICPPASTArrayDeclarator) declarator).addArrayModifier(arrayMod);
            }
        } else {
            declarator = factory.newDeclarator(((IASTSimpleDeclaration) newNode).getDeclarators()[0].getName());
        }

        declarator.setInitializer(((IASTSimpleDeclaration) newNode).getDeclarators()[0].getInitializer());

        declaration.addDeclarator(declarator);
        IASTNode replacement = factory.newDeclarationStatement(declaration);

        rewrite.replace(oldNode, replacement, null);
    }

    protected void doTypeIdChange(ASTRewrite rewrite, char[] aliasName, IASTNode oldNode) {
        // Replace declaration specifier with alias
        ICPPASTDeclSpecifier replacement = factory.newTypedefNameSpecifier(factory.newName(aliasName));
        replacement.setStorageClass(((ICPPASTTypeId) oldNode).getDeclSpecifier().getStorageClass());

        rewrite.replace(oldNode, replacement, null);
    }

    protected IASTNode createAliasStatement(char[] aliasName) {
        IASTNode newNode = refactorSelection.getSelectedNode().copy();

        ICPPASTTypeId typeId = findTypeId(newNode);
        typeId.getDeclSpecifier().setStorageClass(IASTDeclSpecifier.sc_unspecified);

        ICPPASTAliasDeclaration aliasDeclaration = factory.newAliasDeclaration(factory.newName(aliasName), typeId);
        IASTDeclarationStatement aliasDeclarationStatement = factory.newDeclarationStatement(aliasDeclaration);
        return aliasDeclarationStatement;
    }

    protected void placeAliasDeclaration(ASTRewrite rewrite, IASTNode oldNode, IASTNode aliasDeclarationStatement) {
        IASTNode parent = getScope();
        IASTNode insertionPoint = parent.getChildren()[0];

        rewrite.insertBefore(parent, insertionPoint, aliasDeclarationStatement, null);
    }

}
