package com.cevelop.aliextor.ast;

import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;

import com.cevelop.aliextor.ast.selection.IRefactorSelection;


public class FunctionParameterVisitor extends SimpleVisitor {

    public FunctionParameterVisitor(IRefactorSelection refactorSelection) {
        super(refactorSelection);
        shouldVisitParameterDeclarations = true;
    }

    @Override
    public int visit(IASTParameterDeclaration parameterDeclaration) {
        // Takes care of FunctionParameters aka ICPPParameterDeclarator
        if (ASTHelper.isSameIASTParamDeclaration(parameterDeclaration, refactorSelection.getSelectedNode())) {
            occurrences.add(parameterDeclaration);
        }
        return super.visit(parameterDeclaration);
    }

}
