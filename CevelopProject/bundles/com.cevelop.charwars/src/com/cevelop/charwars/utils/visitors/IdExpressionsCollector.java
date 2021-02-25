package com.cevelop.charwars.utils.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;


public class IdExpressionsCollector extends ASTVisitor {

    private List<IASTIdExpression> idExpressions;

    public IdExpressionsCollector() {
        this.shouldVisitExpressions = true;
        this.idExpressions = new ArrayList<>();
    }

    @Override
    public int visit(IASTExpression expression) {
        if (expression instanceof IASTIdExpression) {
            IASTIdExpression idExpression = (IASTIdExpression) expression;
            idExpressions.add(idExpression);
        }
        return PROCESS_CONTINUE;
    }

    public List<IASTIdExpression> getIdExpressions() {
        return idExpressions;
    }
}
