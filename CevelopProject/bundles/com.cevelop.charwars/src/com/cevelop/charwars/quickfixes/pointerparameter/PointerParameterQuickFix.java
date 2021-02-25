package com.cevelop.charwars.quickfixes.pointerparameter;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;

import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.asttools.ASTRewriteCache;
import com.cevelop.charwars.asttools.IndexFinder;
import com.cevelop.charwars.asttools.IndexFinder.IndexFinderInstruction;
import com.cevelop.charwars.constants.ErrorMessages;
import com.cevelop.charwars.constants.QuickFixLabels;
import com.cevelop.charwars.quickfixes.BaseQuickFix;
import com.cevelop.charwars.utils.analyzers.FunctionAnalyzer;
import com.cevelop.charwars.utils.analyzers.UEAnalyzer;


public class PointerParameterQuickFix extends BaseQuickFix {

    @Override
    public String getLabel() {
        return QuickFixLabels.POINTER_PARAMETER;
    }

    @Override
    protected String getErrorMessage() {
        return ErrorMessages.POINTER_PARAMETER_QUICK_FIX;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite rewrite, ASTRewriteCache rewriteCache) {
        final IASTDeclarator paramDeclarator = (IASTDeclarator) markedNode;
        final ICPPASTParameterDeclaration parameterDeclaration = (ICPPASTParameterDeclaration) paramDeclarator.getParent();
        final int paramIndex = FunctionAnalyzer.getParameterIndex(parameterDeclaration);
        final IASTFunctionDeclarator functionDeclarator = (IASTFunctionDeclarator) parameterDeclaration.getParent();

        IndexFinder.findAllOccurrences(functionDeclarator.getName(), rewriteCache, (name, rewrite1) -> {
            handleNode(name, paramIndex, rewrite1);
            return IndexFinderInstruction.CONTINUE_SEARCH;
        });
    }

    private void handleNode(IASTNode functionName, int paramIndex, ASTRewrite rewrite) {
        IASTNode node = functionName;
        while (node != null && !(node instanceof IASTFunctionDefinition) && !(node instanceof IASTFunctionCallExpression) &&
               !(node instanceof IASTSimpleDeclaration)) {

            node = node.getParent();
        }

        if (node instanceof IASTFunctionDefinition) {
            handleFunctionDefinition((IASTFunctionDefinition) node, paramIndex, rewrite);
        } else if (node instanceof IASTFunctionCallExpression) {
            handleFunctionCall((IASTFunctionCallExpression) node, paramIndex, rewrite);
        } else if (node instanceof IASTSimpleDeclaration) {
            handleFunctionDeclaration((IASTSimpleDeclaration) node, paramIndex, rewrite);
        }
    }

    private void handleFunctionDefinition(IASTFunctionDefinition functionDefinition, int paramIndex, ASTRewrite rewrite) {
        final ICPPASTFunctionDeclarator functionDeclarator = (ICPPASTFunctionDeclarator) functionDefinition.getDeclarator();
        final IASTParameterDeclaration oldParamDeclaration = functionDeclarator.getParameters()[paramIndex];
        final IASTDeclarator oldDeclarator = oldParamDeclaration.getDeclarator();
        final ICPPASTDeclarator newDeclarator = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newReferenceDeclarator(oldDeclarator.getName()
                .toString());
        final IASTDeclSpecifier newDeclSpecifier = oldParamDeclaration.getDeclSpecifier().copy();
        final IASTParameterDeclaration newParamDeclaration = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newParameterDeclaration(
                newDeclSpecifier, newDeclarator);
        ASTModifier.replace(oldParamDeclaration, newParamDeclaration, rewrite);

        final IASTStatement functionBody = functionDefinition.getBody();
        final ReplaceInsideFunctionBodyVisitor visitor = new ReplaceInsideFunctionBodyVisitor(rewrite, oldDeclarator.getName());
        functionBody.accept(visitor);
    }

    private void handleFunctionCall(IASTFunctionCallExpression fcExpression, int paramIndex, ASTRewrite rewrite) {
        final IASTNode arg = fcExpression.getArguments()[paramIndex];
        if (UEAnalyzer.isAddressOperatorExpression(arg)) {
            final IASTNode newArg = UEAnalyzer.getOperand(arg).copy();
            ASTModifier.replace(arg, newArg, rewrite);
        } else if (arg instanceof IASTExpression) {
            final IASTExpression expr = (IASTExpression) arg;
            final IASTUnaryExpression newArg = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newDereferenceOperatorExpression(expr.copy());
            ASTModifier.replace(expr, newArg, rewrite);
        }
    }

    private void handleFunctionDeclaration(IASTSimpleDeclaration simpleDeclaration, int paramIndex, ASTRewrite rewrite) {
        for (final IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
            if (declarator instanceof ICPPASTFunctionDeclarator) {
                final ICPPASTFunctionDeclarator functionDeclarator = (ICPPASTFunctionDeclarator) declarator;
                final IASTParameterDeclaration param = functionDeclarator.getParameters()[paramIndex];
                final IASTDeclSpecifier newDeclSpecifier = param.getDeclSpecifier().copy();
                final IASTDeclarator newDeclarator = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newReferenceDeclarator(param.getDeclarator()
                        .getName().toString());
                final IASTParameterDeclaration newParam = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newParameterDeclaration(newDeclSpecifier,
                        newDeclarator);
                ASTModifier.replace(param, newParam, rewrite);
                return;
            }
        }
    }
}
