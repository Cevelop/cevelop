package com.cevelop.templator.plugin.asttools;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;


public class FindFunctionCallExpressionsVisitor extends ASTVisitor {

    public FindFunctionCallExpressionsVisitor() {
        super(true);
    }

    private List<ICPPASTFunctionCallExpression> functionCalls = new ArrayList<>();

    @Override
    public int visit(IASTExpression expression) {
        if (expression instanceof ICPPASTFunctionCallExpression) {
            functionCalls.add((ICPPASTFunctionCallExpression) expression);
        }
        return super.visit(expression);
    }

    public List<ICPPASTFunctionCallExpression> getFunctionCalls() {
        return functionCalls;
    }
}
