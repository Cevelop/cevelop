package com.cevelop.aliextor.refactoring.strategy;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.aliextor.ast.ASTHelper;
import com.cevelop.aliextor.ast.BaseASTVisitor;
import com.cevelop.aliextor.ast.selection.IRefactorSelection;
import com.cevelop.aliextor.refactoring.AliExtorRefactoring;


public abstract class RefactoringStrategy {

    protected final static String CANT_PROC = "Can't process selection";

    protected AliExtorRefactoring proxy;
    protected ICPPNodeFactory     factory;
    protected IRefactorSelection  refactorSelection;
    protected ArrayList<IASTNode> occurrences;

    public RefactoringStrategy(AliExtorRefactoring proxy) {
        this.proxy = proxy;
        factory = proxy.getICPPNodeFactory();
        refactorSelection = proxy.getIRefactoringSelection();
    }

    protected void init() {
        occurrences = findOccurrences();
    }

    private ArrayList<IASTNode> findOccurrences() {
        IASTNode searchScope = getScope();
        BaseASTVisitor visitor = decideVisitor(searchScope);
        searchScope.accept(visitor);
        return visitor.getOccurrences();
    }

    protected void checkSelection(RefactoringStatus status) {
        if (occurrences.size() == 0) {
            status.addFatalError(CANT_PROC);
        }
    }

    abstract protected BaseASTVisitor decideVisitor(IASTNode searchScope);

    protected IASTNode getScope() {
        return ASTHelper.findEnclosingScope(refactorSelection.getSelectedNode());
    }

    protected ICPPASTTypeId findTypeId(IASTNode node) {
        if (node instanceof IASTDeclSpecifier) {
            return factory.newTypeId((IASTDeclSpecifier) node, null);
        } else if (node instanceof IASTSimpleDeclaration) {
            IASTDeclSpecifier specifier = ((IASTSimpleDeclaration) node).getDeclSpecifier();

            IASTDeclarator declarator = factory.newDeclarator(factory.newName());

            IASTPointerOperator[] pointerOperators = ((IASTSimpleDeclaration) node).getDeclarators()[0].getPointerOperators();
            for (IASTPointerOperator poop : pointerOperators) {
                declarator.addPointerOperator(poop);
            }

            return factory.newTypeId(specifier, declarator);
        } else if (node instanceof ICPPASTTypeId) {
            return (ICPPASTTypeId) node;
        } else {
            return null;
        }
    }

    public RefactoringStatus checkInitialConditions(RefactoringStatus status) {
        init();
        return checkTheInitialConditions(status);
    }

    abstract protected RefactoringStatus checkTheInitialConditions(RefactoringStatus status);

    abstract public void collectModifications(ASTRewrite rewrite);

}
