package com.cevelop.aliextor.ast.selection;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.aliextor.ast.ASTHelper;
import com.cevelop.aliextor.ast.ASTHelper.Type;
import com.cevelop.aliextor.refactoring.AliExtorRefactoring;
import com.cevelop.aliextor.refactoring.strategy.FunctionRefactoringConcreteStrategy;
import com.cevelop.aliextor.refactoring.strategy.RefactoringStrategy;
import com.cevelop.aliextor.refactoring.strategy.TemplateAliasRefactoringConcreteStrategy;


public class BasicRefactorSelection implements IRefactorSelection {

    private IASTNode            selection;
    private RefactoringStrategy strategy;

    public BasicRefactorSelection(IASTNode selection) {
        this.selection = selection;
    }

    @Override
    public IASTNode getSelectedNode() {
        return selection;
    }

    @Override
    public void setStrategy(AliExtorRefactoring refactoring) {
        if (ASTHelper.isType(getSelectedNode(), Type.ICPPASTFunctionDefinition) || ASTHelper.isType(getSelectedNode(),
                Type.ICPPASTParameterDeclaration)) {
            strategy = new FunctionRefactoringConcreteStrategy(refactoring);
        } else {
            strategy = new TemplateAliasRefactoringConcreteStrategy(refactoring);
        }
    }

    @Override
    public RefactoringStrategy getStrategy() {
        return strategy;
    }

}
