package com.cevelop.charwars.quickfixes.cstring.cleanup;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.charwars.asttools.ASTRewriteCache;
import com.cevelop.charwars.constants.Algorithm;
import com.cevelop.charwars.constants.Constants;
import com.cevelop.charwars.constants.ErrorMessages;
import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.constants.QuickFixLabels;
import com.cevelop.charwars.quickfixes.BaseQuickFix;


public class CStringCleanupQuickFix extends BaseQuickFix {

    public static final Map<Function, Function> functionMap;

    static {
        functionMap = new HashMap<>();
        functionMap.put(Function.STRSTR, Function.FIND);
        functionMap.put(Function.STRCHR, Function.FIND);
        functionMap.put(Function.STRRCHR, Function.RFIND);
        functionMap.put(Function.STRPBRK, Function.FIND_FIRST_OF);
        functionMap.put(Function.STRCSPN, Function.FIND_FIRST_OF);
        functionMap.put(Function.STRSPN, Function.FIND_FIRST_NOT_OF);
        functionMap.put(Function.MEMCHR, Function.STD_FIND);
    }

    @Override
    public String getLabel() {
        return QuickFixLabels.C_STRING_CLEANUP;
    }

    @Override
    protected String getErrorMessage() {
        return ErrorMessages.C_STRING_CLEANUP_QUICK_FIX;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite rewrite, ASTRewriteCache rewriteCache) {
        final IASTFunctionCallExpression functionCall = (IASTFunctionCallExpression) markedNode;
        final Function inFunction = getInFunction(functionCall);
        final Function outFunction = getOutFunction(inFunction);

        CleanupRefactoring refactoring = null;
        if (inFunction == Function.MEMCHR) {
            refactoring = new MemchrCleanupRefactoring(functionCall, inFunction, outFunction, rewrite);
        } else if (inFunction == Function.STRCSPN || inFunction == Function.STRSPN) {
            refactoring = new SizeCleanupRefactoring(functionCall, inFunction, outFunction, rewrite);
        } else {
            refactoring = new PtrCleanupRefactoring(functionCall, inFunction, outFunction, rewrite);
        }

        refactoring.perform();
        if (inFunction == Function.MEMCHR) {
            headers.add(Algorithm.HEADER_NAME);
        }
    }

    private Function getInFunction(IASTFunctionCallExpression functionCall) {
        final IASTIdExpression functionNameExpr = (IASTIdExpression) functionCall.getFunctionNameExpression();
        final String functionName = functionNameExpr.getName().toString().replaceFirst("^" + Constants.STD_PREFIX, "");

        for (final Function f : functionMap.keySet()) {
            if (f.getName().equals(functionName)) {
                return f;
            }
        }
        return null;
    }

    private Function getOutFunction(Function inFunction) {
        return functionMap.get(inFunction);
    }
}
