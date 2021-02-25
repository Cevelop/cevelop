package com.cevelop.codeanalysator.autosar.quickfix;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;


public class MandatoryLambdaParameterListQuickFix extends BaseQuickFix implements IMarkerResolution {

    public MandatoryLambdaParameterListQuickFix(String label) {
        super(label);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (!(markedNode instanceof ICPPASTLambdaExpression)) {
            return;
        }
        ICPPASTLambdaExpression lambda = (ICPPASTLambdaExpression) markedNode;
        ICPPASTFunctionDeclarator newLambdaDeclarator = createLambdaDeclarator();
        hRewrite.insertBefore(lambda, lambda.getBody(), newLambdaDeclarator, null);
    }

    private ICPPASTFunctionDeclarator createLambdaDeclarator() {
        IASTName astName = factory.newName();
        return factory.newFunctionDeclarator(astName);
    }
}
