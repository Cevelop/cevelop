package com.cevelop.charwars.utils.analyzers;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerClause;


public class ExpressionAnalyzer {

    @SuppressWarnings("restriction")
    public static boolean isConstexprExpression(IASTNode node) {
        return node instanceof ICPPASTInitializerClause && ((ICPPASTInitializerClause) node).getEvaluation().isConstantExpression();
    }
}
