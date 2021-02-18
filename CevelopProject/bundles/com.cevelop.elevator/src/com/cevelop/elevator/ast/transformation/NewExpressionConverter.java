package com.cevelop.elevator.ast.transformation;

import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;


public class NewExpressionConverter {

    private final ICPPASTNewExpression expression;

    public NewExpressionConverter(ICPPASTNewExpression expression) {
        this.expression = expression;
    }

    public ICPPASTNewExpression convert() {
        IASTInitializer initializer = expression.getInitializer();
        IASTInitializerList initList = expression.getTranslationUnit().getASTNodeFactory().newInitializerList();
        ICPPASTNewExpression convertedExpression = expression.copy();
        if (initializer != null && initializer instanceof ICPPASTConstructorInitializer) {
            for (IASTInitializerClause clause : ((ICPPASTConstructorInitializer) initializer).getArguments()) {
                initList.addClause(clause.copy());
            }
        }
        convertedExpression.setInitializer(initList);
        return convertedExpression;
    }
}
