package com.cevelop.aliextor.refactoring.strategy;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.aliextor.ast.ASTHelper;
import com.cevelop.aliextor.ast.BaseASTVisitor;
import com.cevelop.aliextor.ast.PartialSelectionVisitor;
import com.cevelop.aliextor.ast.selection.PartialRefactorSelection;
import com.cevelop.aliextor.refactoring.AliExtorRefactoring;


public class PartialDeclSpecRefactoringConctreteStrategy extends RefactoringStrategy {

    public PartialDeclSpecRefactoringConctreteStrategy(AliExtorRefactoring proxy) {
        super(proxy);
    }

    @Override
    public RefactoringStatus checkTheInitialConditions(RefactoringStatus status) {
        checkSelection(status);

        for (String fatalError : ((PartialRefactorSelection) refactorSelection).getFatalErrors()) {
            status.addFatalError(fatalError);
        }

        return status;
    }

    @Override
    protected BaseASTVisitor decideVisitor(IASTNode searchScope) {
        return new PartialSelectionVisitor(refactorSelection);
    }

    @Override
    public void collectModifications(ASTRewrite rewrite) {
        char[] aliasName = proxy.getTheUserInput().toCharArray();

        boolean hasToRefactorOnlyDeclSpec = ((PartialRefactorSelection) refactorSelection).getTqOfAliasDecl().pointerOperators.size() == 0;

        if (hasToRefactorOnlyDeclSpec) {
            doChangeOfDeclSpecifier(rewrite, aliasName);
        } else {
            doChangeOfDeclaration(rewrite, aliasName);
        }

        IASTDeclSpecifier oldNode = ((PartialRefactorSelection) refactorSelection).getDeclSpec();
        IASTDeclSpecifier copyNode = oldNode.copy(CopyStyle.withLocations);

        // Remove properties from copied decl Specifier
        copyNode.setConst(((PartialRefactorSelection) refactorSelection).getTqOfAliasDecl().isConst);
        copyNode.setVolatile(((PartialRefactorSelection) refactorSelection).getTqOfAliasDecl().isVolatile);
        copyNode.setStorageClass(IASTDeclSpecifier.sc_unspecified);

        // Create TypeId for alias declaration
        ICPPASTTypeId typeId = factory.newTypeId(copyNode, factory.newDeclarator(factory.newName()));

        // Add selected PointerOperators to new type alias
        for (IASTPointerOperator poop : ((PartialRefactorSelection) refactorSelection).getTqOfAliasDecl().pointerOperators) {
            typeId.getAbstractDeclarator().addPointerOperator(poop);
        }

        // Create alias declaration statement
        ICPPASTAliasDeclaration aliasDeclaration = factory.newAliasDeclaration(factory.newName(aliasName), typeId);

        // Add AliasDeclaration to DeclarationStatement
        IASTDeclarationStatement aliasDeclarationStatement = factory.newDeclarationStatement(aliasDeclaration);

        IASTNode parent = ASTHelper.findEnclosingScope(oldNode);
        IASTNode insertionPoint = parent.getChildren()[0];

        // Insert alias declaration statement
        rewrite.insertBefore(parent, insertionPoint, aliasDeclarationStatement, null);
    }

    private void doChangeOfDeclSpecifier(ASTRewrite rewrite, char[] aliasName) {
        if (proxy.shouldJustRefactorSelected()) {
            ICPPASTDeclSpecifier oldNode = (ICPPASTDeclSpecifier) refactorSelection.getSelectedNode();
            ICPPASTNamedTypeSpecifier replacement = replicateDeclSpecifier(aliasName);
            rewrite.replace(oldNode, replacement, null);
        } else {
            assert (occurrences != null);
            for (IASTNode oldNode : occurrences) {
                ICPPASTNamedTypeSpecifier replacement = factory.newTypedefNameSpecifier(factory.newName(aliasName));
                PartialRefactorSelection foundNode = new PartialRefactorSelection((ICPPASTDeclSpecifier) oldNode, null,
                        ((PartialRefactorSelection) refactorSelection).getSelectionString());

                replacement.setConst(wasConstAndHasToBeConstAgain((ICPPASTDeclSpecifier) oldNode));
                replacement.setVolatile(wasVolatileAndHasToBeVolatileAgain((ICPPASTDeclSpecifier) oldNode));
                replacement.setStorageClass(foundNode.getTqOfNamedTypeSpec().storageClass);

                rewrite.replace(oldNode, replacement, null);
            }
        }
    }

    private ICPPASTNamedTypeSpecifier replicateDeclSpecifier(char[] aliasName) {
        ICPPASTNamedTypeSpecifier replacement = factory.newTypedefNameSpecifier(factory.newName(aliasName));
        replacement.setConst(((PartialRefactorSelection) refactorSelection).getTqOfNamedTypeSpec().isConst);
        replacement.setVolatile(((PartialRefactorSelection) refactorSelection).getTqOfNamedTypeSpec().isVolatile);
        replacement.setStorageClass(((PartialRefactorSelection) refactorSelection).getTqOfNamedTypeSpec().storageClass);
        return replacement;
    }

    private void doChangeOfDeclaration(ASTRewrite rewrite, char[] aliasName) {
        if (proxy.shouldJustRefactorSelected()) {

            ICPPASTNamedTypeSpecifier newNamedTypeSpec = replicateDeclSpecifier(aliasName);

            ICPPASTDeclarator oldDeclarator = (ICPPASTDeclarator) ((PartialRefactorSelection) refactorSelection).getDeclarator().copy();

            ICPPASTDeclarator newDeclarator = factory.newDeclarator(oldDeclarator.getName());
            newDeclarator.setInitializer(oldDeclarator.getInitializer());

            for (IASTPointerOperator poop : ((PartialRefactorSelection) refactorSelection).getTqOfNamedTypeSpec().pointerOperators) {
                newDeclarator.addPointerOperator(poop);
            }

            IASTNode oldNode = refactorSelection.getSelectedNode();

            IASTNode replacement = null;
            if (oldNode instanceof IASTSimpleDeclaration) {
                replacement = factory.newSimpleDeclaration(newNamedTypeSpec);
                ((IASTSimpleDeclaration) replacement).addDeclarator(newDeclarator);
            } else if (oldNode instanceof ICPPASTTypeId) {
                replacement = factory.newTypeId(newNamedTypeSpec, newDeclarator);
                ((ICPPASTTypeId) replacement).setAbstractDeclarator(newDeclarator);
            }

            rewrite.replace(oldNode, replacement, null);
        } else {
            assert (occurrences != null);
            for (IASTNode oldNode : occurrences) {

                IASTDeclarator declarator = null;
                IASTDeclSpecifier declSpec = null;

                if (oldNode instanceof ICPPASTNamedTypeSpecifier || oldNode instanceof ICPPASTSimpleDeclSpecifier) {
                    oldNode = oldNode.getParent();
                }

                if (oldNode instanceof IASTSimpleDeclaration) {
                    declarator = ((IASTSimpleDeclaration) oldNode).getDeclarators()[0];
                    declSpec = ((IASTSimpleDeclaration) oldNode).getDeclSpecifier();
                } else if (oldNode instanceof ICPPASTTypeId) {
                    declarator = ((ICPPASTTypeId) oldNode).getAbstractDeclarator();
                    declSpec = ((ICPPASTTypeId) oldNode).getDeclSpecifier();
                }

                ICPPASTNamedTypeSpecifier newNamedTypeSpec = factory.newTypedefNameSpecifier(factory.newName(aliasName));
                PartialRefactorSelection foundNode = new PartialRefactorSelection(declSpec, declarator, ((PartialRefactorSelection) refactorSelection)
                        .getSelectionString());

                newNamedTypeSpec.setConst(wasConstAndHasToBeConstAgain(declSpec));
                newNamedTypeSpec.setVolatile(wasVolatileAndHasToBeVolatileAgain(declSpec));
                newNamedTypeSpec.setStorageClass(foundNode.getTqOfNamedTypeSpec().storageClass);

                IASTNode replacement = null;

                ICPPASTDeclarator oldDeclarator = (ICPPASTDeclarator) foundNode.getDeclarator().copy();

                ICPPASTDeclarator newDeclarator = factory.newDeclarator(oldDeclarator.getName());
                newDeclarator.setInitializer(oldDeclarator.getInitializer());

                for (IASTPointerOperator poop : foundNode.getTqOfNamedTypeSpec().pointerOperators) {
                    newDeclarator.addPointerOperator(poop);
                }

                if (oldNode instanceof IASTSimpleDeclaration) {
                    replacement = factory.newSimpleDeclaration(newNamedTypeSpec);
                    ((IASTSimpleDeclaration) replacement).addDeclarator(newDeclarator);

                } else if (oldNode instanceof ICPPASTTypeId) {
                    replacement = factory.newTypeId(newNamedTypeSpec, newDeclarator);
                }

                rewrite.replace(oldNode, replacement, null);
            }
        }
    }

    private boolean wasVolatileAndHasToBeVolatileAgain(IASTDeclSpecifier declSpec) {
        return declSpec.isVolatile() && ((PartialRefactorSelection) refactorSelection).getTqOfNamedTypeSpec().isVolatile;
    }

    private boolean wasConstAndHasToBeConstAgain(IASTDeclSpecifier declSpec) {
        return declSpec.isConst() && ((PartialRefactorSelection) refactorSelection).getTqOfNamedTypeSpec().isConst;
    }

}
