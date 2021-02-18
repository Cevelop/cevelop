package com.cevelop.codeanalysator.autosar.quickfix;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.dom.rewrite.DeclarationGenerator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.core.resources.IMarker;

import com.cevelop.codeanalysator.autosar.util.AutoHelper;
import com.cevelop.codeanalysator.autosar.util.ContextFlagsHelper;
import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;
import com.cevelop.codeanalysator.core.util.CodeAnalysatorDeclarationGeneratorImpl;


public class ReplaceAutoWithDeducedTypeQuickFix extends BaseQuickFix {

    public ReplaceAutoWithDeducedTypeQuickFix(String label) {
        super(label);
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        if (!super.isApplicable(marker)) return false;

        String contextFlagsString = getProblemArgument(marker, ContextFlagsHelper.UseAutoSparinglyContextFlagsStringIndex);
        return !contextFlagsString.contains(ContextFlagsHelper.UseAutoSparinglyContextFlagControlDeclaration);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (!(markedNode instanceof IASTDeclaration)) return;
        IASTDeclaration declaration = (IASTDeclaration) markedNode;
        if (declaration instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
            if (!(AutoHelper.isAutoDeclaringVariables(simpleDeclaration) && !AutoHelper
                    .isInitializingVariablesWithFunctionCallOrInitializerOfNonFundamentalType(simpleDeclaration))) {
                return;
            }
            replaceAutoVariableDeclarationWithDeducedType(simpleDeclaration, hRewrite);
        } else if (declaration instanceof IASTFunctionDefinition) {
            IASTFunctionDefinition functionDefinition = (IASTFunctionDefinition) declaration;
            if (!(AutoHelper.isAutoDeclaringFunction(functionDefinition) && !AutoHelper.isDeclaringTemplateFunctionWithTrailingReturnTypeSyntax(
                    functionDefinition))) {
                return;
            }
            replaceAutoReturnTypeWithDeducedType(functionDefinition, hRewrite);
        }
    }

    private void replaceAutoVariableDeclarationWithDeducedType(IASTSimpleDeclaration simpleDeclaration, ASTRewrite hRewrite) {
        IASTDeclSpecifier declSpecifier = simpleDeclaration.getDeclSpecifier();
        if (!(declSpecifier instanceof IASTSimpleDeclSpecifier)) return;
        IASTSimpleDeclSpecifier autoSpecifier = (IASTSimpleDeclSpecifier) declSpecifier;

        IASTDeclarator[] declarators = simpleDeclaration.getDeclarators();
        List<IASTNode> deducedDeclarations = Arrays.stream(declarators) //
                .map(declarator -> deduceDeclaration(autoSpecifier, declarator)) //
                .collect(Collectors.toList());
        replaceNode(simpleDeclaration, deducedDeclarations, hRewrite);
    }

    private void replaceNode(IASTNode node, List<? extends IASTNode> replacementNodes, ASTRewrite hRewrite) {
        if (replacementNodes.size() == 1) {
            hRewrite.replace(node, replacementNodes.get(0), null);
        } else {
            replacementNodes.forEach(replacementNode -> hRewrite.insertBefore(node.getParent(), node, replacementNode, null));
            hRewrite.remove(node, null);
        }
    }

    private void replaceAutoReturnTypeWithDeducedType(IASTFunctionDefinition functionDefinition, ASTRewrite hRewrite) {
        IASTDeclSpecifier functionDeclSpecifier = functionDefinition.getDeclSpecifier();
        if (!(functionDeclSpecifier instanceof IASTSimpleDeclSpecifier)) return;
        IASTSimpleDeclSpecifier autoSpecifier = (IASTSimpleDeclSpecifier) functionDeclSpecifier;

        IBinding binding = functionDefinition.getDeclarator().getName().resolveBinding();
        if (!(binding instanceof IFunction)) return;
        IFunction function = (IFunction) binding;

        IType returnType = function.getType().getReturnType();
        IASTDeclSpecifier deducedDeclSpecifier = createdDeducedDeclSpecifier(autoSpecifier, returnType);
        removeTrailingReturnType(functionDefinition, hRewrite);
        hRewrite.replace(functionDeclSpecifier, deducedDeclSpecifier, null);
    }

    private void removeTrailingReturnType(IASTFunctionDefinition functionDefinition, ASTRewrite hRewrite) {
        IASTFunctionDeclarator functionDeclarator = functionDefinition.getDeclarator();
        if (!(functionDeclarator instanceof ICPPASTFunctionDeclarator)) return;
        ICPPASTFunctionDeclarator cppFunctionDeclarator = (ICPPASTFunctionDeclarator) functionDeclarator;

        if (cppFunctionDeclarator.getTrailingReturnType() != null) {
            ICPPASTFunctionDeclarator newCppFunctionDeclarator = cppFunctionDeclarator.copy();
            newCppFunctionDeclarator.setTrailingReturnType(null);
            hRewrite.replace(cppFunctionDeclarator, newCppFunctionDeclarator, null);
        }
    }

    private IASTSimpleDeclaration deduceDeclaration(IASTSimpleDeclSpecifier autoSpecifier, IASTDeclarator declarator) {
        IASTDeclSpecifier newDeclSpecifier;
        IASTDeclarator newDeclarator = declarator.copy();
        IBinding binding = declarator.getName().resolveBinding();
        if (binding instanceof IVariable) {
            IVariable variable = (IVariable) binding;

            IType variableType = variable.getType();
            newDeclSpecifier = createdDeducedDeclSpecifier(autoSpecifier, variableType);
            if (variableType instanceof ICPPReferenceType && !((ICPPReferenceType) variableType).isRValueReference()) {
                changeToLValueReference(newDeclarator);
            }
        } else {
            newDeclSpecifier = autoSpecifier.copy(); /* fallback */
        }
        IASTSimpleDeclaration newSimpleDeclaration = factory.newSimpleDeclaration(newDeclSpecifier);
        newSimpleDeclaration.addDeclarator(newDeclarator);
        return newSimpleDeclaration;
    }

    private IASTDeclSpecifier createdDeducedDeclSpecifier(IASTSimpleDeclSpecifier autoSpecifier, IType deducedType) {
        DeclarationGenerator declarationGenerator = new CodeAnalysatorDeclarationGeneratorImpl(factory);
        IASTDeclSpecifier deducedDeclSpecifier = declarationGenerator.createDeclSpecFromType(deducedType);
        deducedDeclSpecifier.setStorageClass(autoSpecifier.getStorageClass());
        return deducedDeclSpecifier;
    }

    @SuppressWarnings("restriction")
    private void changeToLValueReference(IASTDeclarator declarator) {
        if (!(declarator instanceof CPPASTDeclarator)) {
            return;
        } /* cannot remove pointer operator */
        CPPASTDeclarator cppDeclarator = (CPPASTDeclarator) declarator;

        Arrays.stream(cppDeclarator.getPointerOperators()) //
                .filter(op -> op instanceof ICPPASTReferenceOperator && ((ICPPASTReferenceOperator) op).isRValueReference()) //
                .findAny().ifPresent(rValueReferenceOp -> {
                    cppDeclarator.removePointerOperator(rValueReferenceOp);
                    cppDeclarator.addPointerOperator(factory.newReferenceOperator(false));
                });
    }
}
