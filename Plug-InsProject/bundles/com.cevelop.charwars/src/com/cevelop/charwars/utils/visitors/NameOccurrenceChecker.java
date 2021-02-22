package com.cevelop.charwars.utils.visitors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;


public class NameOccurrenceChecker extends ASTVisitor {

    private String  name;
    private boolean nameOccurred;

    public NameOccurrenceChecker(String name) {
        this.shouldVisitExpressions = true;
        this.shouldVisitDeclarators = true;
        this.name = name;
        this.nameOccurred = false;
    }

    @Override
    public int visit(IASTExpression expression) {
        if (expression instanceof IASTIdExpression) {
            IASTIdExpression idExpression = (IASTIdExpression) expression;
            if (idExpression.getName().toString().equals(name)) {
                nameOccurred = true;
                return PROCESS_ABORT;
            }
        }
        return PROCESS_CONTINUE;
    }

    @Override
    public int visit(IASTDeclarator declarator) {
        if (declarator.getName().toString().equals(name)) {
            nameOccurred = true;
            return PROCESS_ABORT;
        }
        return PROCESS_CONTINUE;
    }

    public boolean hasNameOccurred() {
        return nameOccurred;
    }
}
