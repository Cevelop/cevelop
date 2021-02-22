package com.cevelop.charwars.quickfixes.cstr;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.asttools.ASTRewriteCache;
import com.cevelop.charwars.constants.ErrorMessages;
import com.cevelop.charwars.constants.QuickFixLabels;
import com.cevelop.charwars.quickfixes.BaseQuickFix;


public class CStrQuickFix extends BaseQuickFix {

    @Override
    public String getLabel() {
        String functionSignature = getProblemArgument(currentMarker, 0);
        return QuickFixLabels.C_STR + functionSignature;
    }

    @Override
    protected String getErrorMessage() {
        return ErrorMessages.C_STR_QUICK_FIX;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite rewrite, ASTRewriteCache rewriteCache) {
        IASTFunctionCallExpression cStrCall = (IASTFunctionCallExpression) markedNode;
        IASTNode stdString = ASTAnalyzer.extractStdStringArg(cStrCall);
        ASTModifier.replace(cStrCall, stdString, rewrite);
    }
}
