package com.cevelop.codeanalysator.autosar.quickfix;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.dom.rewrite.DeclarationGenerator;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;

import com.cevelop.codeanalysator.autosar.util.LambdaHelper;
import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;
import com.cevelop.codeanalysator.core.util.CodeAnalysatorDeclarationGeneratorImpl;


public class NoImplicitLambdaReturnTypeQuickFix extends BaseQuickFix {

    public NoImplicitLambdaReturnTypeQuickFix(String label) {
        super(label);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (!(markedNode instanceof ICPPASTLambdaExpression)) return;
        ICPPASTLambdaExpression lambda = (ICPPASTLambdaExpression) markedNode;

        ICPPASTFunctionDeclarator lambdaDeclarator = lambda.getDeclarator();
        IType returnType = LambdaHelper.getLambdaReturnType(lambda);
        ICPPASTFunctionDeclarator newLambdaDeclarator = createExplicitLambdaDeclarator(lambdaDeclarator, returnType);
        if (lambdaDeclarator != null) {
            hRewrite.replace(lambdaDeclarator, newLambdaDeclarator, null);
        } else {
            hRewrite.insertBefore(lambda, lambda.getBody(), newLambdaDeclarator, null);
        }
    }

    private ICPPASTFunctionDeclarator createExplicitLambdaDeclarator(ICPPASTFunctionDeclarator lambdaDeclarator, IType returnType) {
        DeclarationGenerator generator = new CodeAnalysatorDeclarationGeneratorImpl(factory);

        IASTDeclSpecifier typeSpec = generator.createDeclSpecFromType(returnType);
        IASTDeclarator declarator = generator.createDeclaratorFromType(returnType, CharArrayUtils.EMPTY);
        IASTTypeId typeId = factory.newTypeId(typeSpec, declarator);

        ICPPASTFunctionDeclarator newLambdaDeclarator;
        if (lambdaDeclarator != null) {
            newLambdaDeclarator = lambdaDeclarator.copy(CopyStyle.withLocations);
        } else {
            IASTName astName = factory.newName();
            newLambdaDeclarator = factory.newFunctionDeclarator(astName);
        }
        newLambdaDeclarator.setTrailingReturnType(typeId);
        return newLambdaDeclarator;
    }
}
