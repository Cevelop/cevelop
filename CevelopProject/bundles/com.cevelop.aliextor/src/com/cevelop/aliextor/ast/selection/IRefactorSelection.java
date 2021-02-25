package com.cevelop.aliextor.ast.selection;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.aliextor.refactoring.AliExtorRefactoring;
import com.cevelop.aliextor.refactoring.strategy.RefactoringStrategy;


public interface IRefactorSelection {

    IASTNode getSelectedNode();

    void setStrategy(AliExtorRefactoring refactoring);

    RefactoringStrategy getStrategy();
}
