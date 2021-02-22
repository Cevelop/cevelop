package com.cevelop.codeanalysator.autosar.visitor;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.codeanalysator.autosar.util.AliasFinder;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


@SuppressWarnings("restriction")
public class DoNotReturnReferenceToParameterPassedByConstReferenceVisitor extends CodeAnalysatorVisitor {

    public DoNotReturnReferenceToParameterPassedByConstReferenceVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarations = true;
    }

    @Override
    public int visit(IASTDeclaration declaration) {
        if (declaration instanceof IASTFunctionDefinition) {
            IASTFunctionDefinition functionDefinition = (IASTFunctionDefinition) declaration;
            IBinding binding = functionDefinition.getDeclarator().getName().resolveBinding();
            if (binding instanceof IFunction) {
                IFunction function = (IFunction) binding;
                checkFunction(function, functionDefinition.getTranslationUnit());
            }
        }
        return PROCESS_CONTINUE;
    }

    private void checkFunction(IFunction function, IASTTranslationUnit translationUnit) {
        AliasFinder aliasFinder = new AliasFinder(translationUnit);
        if (doesReturnReference(function)) {
            getParametersPassedByReferenceToConst(function).stream() //
                    .forEach(parameter -> checkVariableForReference(parameter, aliasFinder));
        } else if (doesReturnPointer(function)) {
            getParametersPassedByReferenceToConst(function).stream() //
                    .forEach(parameter -> checkVariableForAddressOf(parameter, aliasFinder));
        }
    }

    private boolean doesReturnReference(IFunction function) {
        IType returnType = function.getType().getReturnType();
        return isReferenceType(returnType);
    }

    private boolean isReferenceType(IType aliasType) {
        aliasType = SemanticUtil.getNestedType(aliasType, SemanticUtil.TDEF);
        return aliasType instanceof ICPPReferenceType;
    }

    private boolean doesReturnPointer(IFunction function) {
        IType returnType = function.getType().getReturnType();
        return isPointerType(returnType);
    }

    private boolean isPointerType(IType type) {
        type = SemanticUtil.getNestedType(type, SemanticUtil.TDEF);
        return type instanceof IPointerType;
    }

    private Collection<IVariable> getParametersPassedByReferenceToConst(IFunction function) {
        return Arrays.stream(function.getParameters()) //
                .filter(this::isParameterPassedByReferenceToConst) //
                .collect(Collectors.toList());
    }

    private boolean isParameterPassedByReferenceToConst(IParameter parameter) {
        IType parameterType = parameter.getType();
        return isReferenceToConstType(parameterType);
    }

    private boolean isReferenceToConstType(IType type) {
        type = SemanticUtil.getNestedType(type, SemanticUtil.TDEF);
        if (type instanceof ICPPReferenceType) {
            ICPPReferenceType referenceType = (ICPPReferenceType) type;
            IType referencedType = referenceType.getType();
            if (referencedType instanceof IQualifierType) {
                IQualifierType qualifierType = (IQualifierType) referencedType;
                return qualifierType.isConst();
            }
        }
        return false;
    }

    private void checkVariableForReference(IVariable variable, AliasFinder aliasFinder) {
        aliasFinder.getUsesOfAlias(variable) //
                .forEach(variableUse -> checkVariableUseForReference(variableUse, aliasFinder));
    }

    private void checkVariableUseForReference(IASTExpression variableUse, AliasFinder aliasFinder) {
        IASTNode parent = variableUse.getParent();
        if (parent instanceof IASTReturnStatement) {
            reportRuleForNode(parent);
        } else {
            aliasFinder.findAlias(variableUse) //
                    .filter(alias -> isReferenceType(alias.getType())) //
                    .ifPresent(alias -> checkVariableForReference(alias, aliasFinder));
        }
    }

    private void checkVariableForAddressOf(IVariable variable, AliasFinder aliasFinder) {
        aliasFinder.getUsesOfAlias(variable) //
                .forEach(variableUse -> checkVariableUseForAddressOf(variableUse, aliasFinder));
    }

    private void checkVariableUseForAddressOf(IASTIdExpression parameterUse, AliasFinder aliasFinder) {
        IASTNode parent = parameterUse.getParent();
        if (parent instanceof IASTUnaryExpression) {
            IASTUnaryExpression unaryExpression = (IASTUnaryExpression) parent;
            if (unaryExpression.getOperator() == IASTUnaryExpression.op_amper) {
                checkVariableUseForPointer(unaryExpression, aliasFinder);
            }
        }
    }

    private void checkVariableUseForPointer(IASTExpression variableUse, AliasFinder aliasFinder) {
        IASTNode parent = variableUse.getParent();
        if (parent instanceof IASTReturnStatement) {
            reportRuleForNode(parent);
        } else {
            aliasFinder.findAlias(variableUse) //
                    .filter(alias -> isPointerType(alias.getType())) //
                    .ifPresent(alias -> checkVariableForPointer(alias, aliasFinder));
        }
    }

    private void checkVariableForPointer(IVariable variable, AliasFinder aliasFinder) {
        aliasFinder.getUsesOfAlias(variable) //
                .forEach(variableUse -> checkVariableUseForPointer(variableUse, aliasFinder));
    }
}
