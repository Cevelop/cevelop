package com.cevelop.gslator.checkers.visitors.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeleteExpression;


public class DeleteStatementsVisitor extends ASTVisitor {

    List<ICPPASTDeleteExpression> deleteExpressions;

    public DeleteStatementsVisitor() {
        deleteExpressions = new ArrayList<>();
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(final IASTExpression expression) {
        if (expression instanceof ICPPASTDeleteExpression) {
            deleteExpressions.add((ICPPASTDeleteExpression) expression);
        }
        return super.visit(expression);
    }

    public List<ICPPASTDeleteExpression> getDeleteExpressions() {
        return deleteExpressions;
    }

}
