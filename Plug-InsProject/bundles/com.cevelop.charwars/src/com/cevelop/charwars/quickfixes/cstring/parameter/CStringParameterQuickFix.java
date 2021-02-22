package com.cevelop.charwars.quickfixes.cstring.parameter;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.charwars.asttools.ASTRewriteCache;
import com.cevelop.charwars.constants.ErrorMessages;
import com.cevelop.charwars.constants.QuickFixLabels;
import com.cevelop.charwars.constants.StdString;
import com.cevelop.charwars.quickfixes.BaseQuickFix;


public class CStringParameterQuickFix extends BaseQuickFix {

    private RewriteStrategy rewriteStrategy;

    @Override
    public String getLabel() {
        return QuickFixLabels.C_STRING_PARAMETER;
    }

    @Override
    protected String getErrorMessage() {
        return ErrorMessages.C_STRING_PARAMETER_QUICK_FIX;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite rewrite, ASTRewriteCache rewriteCache) {
        final ICPPASTParameterDeclaration parameterDeclaration = (ICPPASTParameterDeclaration) markedNode.getParent();
        rewriteStrategy = RewriteStrategyFactory.createRewriteStrategy(parameterDeclaration, rewriteCache);
        rewriteStrategy.addStdStringOverload();
        rewriteStrategy.adaptCStringOverload();
        rewriteStrategy.addNewDeclarations();
        headers.add(StdString.HEADER_NAME);
    }
}
