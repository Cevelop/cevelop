package com.cevelop.charwars.quickfixes.cstring.cleanup;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.asttools.CheckAnalyzer;
import com.cevelop.charwars.constants.StringType;
import com.cevelop.charwars.utils.ComplementaryNodeFactory;
import com.cevelop.charwars.utils.analyzers.BEAnalyzer;


public class SizeReturnValueVisitor extends ASTVisitor {

    private IASTName   name;
    private ASTRewrite rewrite;

    public SizeReturnValueVisitor(IASTName name, ASTRewrite rewrite) {
        this.shouldVisitExpressions = true;
        this.name = name;
        this.rewrite = rewrite;
    }

    @Override
    public int leave(IASTExpression expression) {
        if (expression instanceof IASTIdExpression) {
            IASTIdExpression idExpression = (IASTIdExpression) expression;
            if (ASTAnalyzer.isSameName(idExpression.getName(), name)) {
                handleSizeReturnType(idExpression);
            }
        }
        return PROCESS_CONTINUE;
    }

    private void handleSizeReturnType(IASTIdExpression idExpression) {
        if (CheckAnalyzer.isNodeComparedToStrlen(idExpression)) {
            IASTExpression strlenCall = BEAnalyzer.getOtherOperand(idExpression);
            ASTModifier.replace(strlenCall, ComplementaryNodeFactory.newNposExpression(StringType.STRING), rewrite);
        }
    }
}
