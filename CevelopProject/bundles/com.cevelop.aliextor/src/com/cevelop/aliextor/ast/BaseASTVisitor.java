package com.cevelop.aliextor.ast;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.aliextor.ast.selection.IRefactorSelection;


public class BaseASTVisitor extends ASTVisitor {

    protected IRefactorSelection  refactorSelection;
    protected ArrayList<IASTNode> occurrences;

    public BaseASTVisitor(IRefactorSelection refactorSelection) {
        this.refactorSelection = refactorSelection;
        occurrences = new ArrayList<>();
    }

    public ArrayList<IASTNode> getOccurrences() {
        return occurrences;
    }

}
