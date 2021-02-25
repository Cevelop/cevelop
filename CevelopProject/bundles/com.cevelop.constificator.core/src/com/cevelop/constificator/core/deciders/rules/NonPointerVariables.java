package com.cevelop.constificator.core.deciders.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTImplicitNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTRangeBasedForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBasicType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPDeferredFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClosureType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariable;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPEvaluation;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.semantic.Expression;
import com.cevelop.constificator.core.util.semantic.Expression.OperatorSide;
import com.cevelop.constificator.core.util.semantic.Function;
import com.cevelop.constificator.core.util.semantic.Type;
import com.cevelop.constificator.core.util.structural.Node;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.core.util.type.Pair;


@SuppressWarnings("restriction")
public class NonPointerVariables {

    public static boolean addressIsAssignedToPointerToNonConst(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(IASTInitializer.class, name, (IASTInitializer initializer, IASTName reference) -> {
            ICPPASTDeclarator declarator = Relation.getAncestorOf(ICPPASTDeclarator.class, initializer);

            if (declarator == null) {
                return false;
            }

            IASTPointerOperator[] pointerOperators = declarator.getPointerOperators();

            if (pointerOperators == null || pointerOperators.length != 1) {
                return false;
            }

            IASTPointer pointerOperator = Cast.as(IASTPointer.class, pointerOperators[0]);

            if (pointerOperator == null) {
                return false;
            }

            IASTSimpleDeclaration declaration = Relation.getAncestorOf(IASTSimpleDeclaration.class, declarator);

            return !declaration.getDeclSpecifier().isConst();
        }, cache);
    }

    public static Pair<Boolean, Pair<IASTName, Integer>> addressIsPassedAsPointerToNonConst(ICPPASTName name, ASTRewriteCache cache) {
        Pair<Boolean, Pair<IASTName, Integer>> result = new Pair<>(false, new Pair<>(null, -1));

        Boolean violates = Node.anyOfDescendingFrom(ICPPASTFunctionCallExpression.class, name, (ICPPASTFunctionCallExpression expression,
                IASTName reference) -> {

            if (Relation.isDescendendOf(ICPPASTFieldReference.class, reference)) {
                return false;
            }

            ICPPASTUnaryExpression unary = Relation.getAncestorOf(ICPPASTUnaryExpression.class, reference);

            if (unary == null || unary.getOperator() != IASTUnaryExpression.op_amper) {
                return false;
            }

            ArrayList<Integer> argumentIndexes = new ArrayList<>();
            IASTInitializerClause[] arguments = expression.getArguments();

            for (int i = 0; i < arguments.length; ++i) {
                ICPPASTUnaryExpression argument = Cast.as(ICPPASTUnaryExpression.class, arguments[i]);

                IASTIdExpression idExpression = Relation.getDescendendOf(IASTIdExpression.class, argument);

                if (idExpression != null && idExpression.getName().getBinding().equals(reference.getBinding())) {
                    argumentIndexes.add(i);
                }
            }

            IASTName functionASTName;
            IASTExpression functionName = expression.getFunctionNameExpression();
            if (functionName instanceof IASTIdExpression) {
                functionASTName = ((IASTIdExpression) functionName).getName();
            } else if (functionName instanceof ICPPASTFieldReference) {
                functionASTName = ((ICPPASTFieldReference) functionName).getFieldName();
            } else {
                return false;
            }
            ICPPFunction function = Cast.as(ICPPFunction.class, functionASTName.resolveBinding());

            if (function != null) {
                ICPPParameter[] parameters = function.getParameters();

                for (int index : argumentIndexes) {
                    ICPPParameter parameter = parameters[index];

                    if (!(parameter.getType() instanceof IPointerType)) {
                        continue;
                    }

                    IPointerType parameterType = (IPointerType) parameter.getType();
                    IQualifierType qualifiedType = Cast.as(IQualifierType.class, parameterType.getType());

                    if (qualifiedType == null) {
                        result.second().first(functionASTName);
                        result.second().second(index);
                        return true;
                    }
                }
            }

            return false;
        }, cache);

        result.first(violates);
        return result;
    }

    public static boolean addressIsPassedAsReferenceToPointerToNonConst(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTFunctionCallExpression.class, name, (ICPPASTFunctionCallExpression expression, IASTName reference) -> {
            if (Relation.isDescendendOf(ICPPASTFieldReference.class, reference)) {
                return false;
            }

            ICPPASTUnaryExpression unary = Relation.getAncestorOf(ICPPASTUnaryExpression.class, reference);

            if (unary == null || unary.getOperator() != IASTUnaryExpression.op_amper) {
                return false;
            }

            ArrayList<Integer> argumentIndexes = new ArrayList<>();
            IASTInitializerClause[] arguments = expression.getArguments();

            for (int i = 0; i < arguments.length; ++i) {
                ICPPASTUnaryExpression argument = Cast.as(ICPPASTUnaryExpression.class, arguments[i]);

                IASTIdExpression idExpression = Relation.getDescendendOf(IASTIdExpression.class, argument);

                if (idExpression != null && idExpression.getName().getBinding().equals(reference.getBinding())) {
                    argumentIndexes.add(i);
                }
            }

            IBinding binding;
            IASTExpression functionName = expression.getFunctionNameExpression();
            if (functionName instanceof IASTIdExpression) {
                binding = ((IASTIdExpression) functionName).getName().resolveBinding();
            } else if (functionName instanceof ICPPASTFieldReference) {
                binding = ((ICPPASTFieldReference) functionName).getFieldName().resolveBinding();
            } else {
                return false;
            }
            ICPPFunction function = Cast.as(ICPPFunction.class, binding);

            if (function != null) {
                ICPPParameter[] parameters = function.getParameters();

                for (int index : argumentIndexes) {
                    ICPPParameter parameter = parameters[index];

                    if (!(parameter.getType() instanceof ICPPReferenceType)) {
                        continue;
                    }

                    ICPPReferenceType topLevelParameterType = (ICPPReferenceType) parameter.getType();

                    if (!(topLevelParameterType.getType() instanceof IPointerType)) {
                        continue;
                    }

                    IPointerType referencedType = (IPointerType) topLevelParameterType.getType();
                    IQualifierType qualifiedType = Cast.as(IQualifierType.class, referencedType.getType());

                    if (qualifiedType == null) {
                        return true;
                    }
                }
            }

            return false;
        }, cache);
    }

    public static boolean addressIsUsedToBindReferenceToPointerToNonConst(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(IASTInitializer.class, name, (IASTInitializer initializer, IASTName reference) -> {
            ICPPASTDeclarator declarator = Relation.getAncestorOf(ICPPASTDeclarator.class, initializer);
            IASTSimpleDeclaration declaration = Relation.getAncestorOf(IASTSimpleDeclaration.class, declarator);
            ICPPASTUnaryExpression unary = Relation.getAncestorOf(ICPPASTUnaryExpression.class, reference);

            return unary != null && unary.getOperator() == IASTUnaryExpression.op_amper && declarator != null && declarator
                    .getPointerOperators().length == 2 && Node.declaresLValueReferenceToPointer(declarator) && !declaration.getDeclSpecifier()
                            .isConst();
        }, cache);
    }

    public static boolean isOperandInModifyingBinaryExpression(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTBinaryExpression.class, name, (ICPPASTBinaryExpression binary, IASTName reference) -> {
            CPPVariable variable = Cast.as(CPPVariable.class, reference.getBinding());
            if (variable != null) {
                if (SemanticUtil.getSimplifiedType(variable.getType()) instanceof IPointerType && !Expression.isDereferencedNTimes(reference, 0)) {
                    return false;
                }
            }

            return (Expression.isLeftHandOperandOf((ICPPASTName) reference, binary) && Expression.isModifyingOperation(binary,
                    OperatorSide.LeftHandSide)) || (Expression.isRightHandOperandOf((ICPPASTName) reference, binary) && Expression
                            .isModifyingOperation(binary, OperatorSide.RightHandSide));
        }, cache);
    }

    public static boolean isOperandInModifyingUnaryExpression(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTUnaryExpression.class, name, (ICPPASTUnaryExpression unary, IASTName reference) -> {
            CPPVariable variable = Cast.as(CPPVariable.class, reference.getBinding());
            if (variable != null) {
                if (SemanticUtil.getSimplifiedType(variable.getType()) instanceof IPointerType && !Expression.isDereferencedNTimes(reference, 0)) {
                    return false;
                }
            }

            IASTName lhs;
            if ((lhs = Relation.getDescendendOf(ICPPASTName.class, unary.getOperand())) == null) {
                return false;
            }
            return lhs.resolveBinding().equals(name.resolveBinding()) && Expression.isModifyingOperation(unary);
        }, cache);
    }

    public static Pair<Boolean, Pair<IASTName, Integer>> isPassedAsLValueReferenceToNonConst(ICPPASTName name, ASTRewriteCache cache) {
        Pair<Boolean, Pair<IASTName, Integer>> result = new Pair<>(false, new Pair<>(null, -1));

        // TODO: Clean up this mess!

        Boolean violatesViaFunctionCall = Node.anyOfDescendingFrom(ICPPASTFunctionCallExpression.class, name, (
                ICPPASTFunctionCallExpression expression, IASTName reference) -> {
            List<Integer> indices = Function.getArgumentIndicesFor(expression.getArguments(), name, (n) -> {
                return Expression.isDereferencedNTimes(n, 0);
            });

            if (indices.size() == 0) {
                return false;
            }

            ICPPASTName functionName;
            if ((functionName = Node.getNameForFunction(expression.getFunctionNameExpression())) == null) {
                return true;
            }

            ICPPFunction function;
            if ((function = Cast.as(ICPPFunction.class, functionName.resolveBinding())) == null) {
                return true;
            }

            if (function instanceof ICPPDeferredFunction) {
                return true;
            }

            if (function.getParameters().length <= indices.get(indices.size() - 1)) {
                return true;
            }

            if (function instanceof ICPPUnknownBinding) {
                return true;
            }

            if (function != null) {
                ICPPParameter[] parameters = function.getParameters();

                for (int index : indices) {
                    ICPPParameter parameter = parameters[index];

                    if (!(parameter.getType() instanceof ICPPReferenceType) || ((ICPPReferenceType) parameter.getType()).isRValueReference()) {
                        continue;
                    }

                    ICPPReferenceType parameterType = (ICPPReferenceType) parameter.getType();

                    if (!Type.isConst(parameterType, 1)) {
                        result.second().first(functionName);
                        result.second().second(index);
                        return true;
                    }
                }
            }

            return false;
        }, cache);

        Boolean violatesViaConstructorCall = Node.anyOfDescendingFrom(ICPPASTConstructorInitializer.class, name, (
                ICPPASTConstructorInitializer initializer, IASTName reference) -> {
            List<Integer> indices = Function.getArgumentIndicesFor(initializer.getArguments(), name, (n) -> {
                return Expression.isDereferencedNTimes(n, 0);
            });

            if (indices.isEmpty()) {
                return false;
            }

            if (!(initializer.getParent() instanceof IASTImplicitNameOwner)) {
                return false;
            }

            IASTImplicitName[] ctorNames = ((IASTImplicitNameOwner) initializer.getParent()).getImplicitNames();
            if (ctorNames.length == 0) {
                return false;
            }

            ICPPConstructor ctor = Cast.as(ICPPConstructor.class, ctorNames[0].getBinding());
            if (ctor == null) {
                return false;
            }

            ICPPParameter[] ctorParameters = ctor.getParameters();
            if (ctorParameters.length < indices.get(indices.size() - 1)) {
                return false;
            }

            for (Integer index : indices) {
                ICPPParameter parameter = ctorParameters[index];
                IType type = parameter.getType();
                if (!Type.isReference(type)) {
                    return false;
                }

                if (Type.isRValueReference(type)) {
                    return true;
                }

                if (!Type.isConst(type, 1)) {
                    result.second().first(ctorNames[0]);
                    result.second().second(index);
                    return true;
                }
            }

            return false;
        }, cache);

        Boolean violatesViaInitializerList = Node.anyOfDescendingFrom(ICPPASTInitializerList.class, name, (ICPPASTInitializerList list,
                IASTName reference) -> {

            List<Integer> indices = Function.getArgumentIndicesFor(list.getClauses(), name, (n) -> {
                return Expression.isDereferencedNTimes(n, 0);
            });
            if (indices.isEmpty()) {
                return false;
            }

            if (!(list.getParent() instanceof IASTImplicitNameOwner)) {
                return false;
            }

            IASTImplicitName[] ctorNames = ((IASTImplicitNameOwner) list.getParent()).getImplicitNames();
            if (ctorNames.length == 0) {
                return false;
            }

            ICPPConstructor ctor = Cast.as(ICPPConstructor.class, ctorNames[0].getBinding());
            if (ctor == null) {
                return false;
            }

            ICPPParameter[] ctorParameters = ctor.getParameters();
            if (ctorParameters.length < indices.get(indices.size() - 1)) {
                return false;
            }

            for (Integer index : indices) {
                ICPPParameter parameter = ctorParameters[index];
                IType type = parameter.getType();
                if (!Type.isReference(type)) {
                    return false;
                }

                if (Type.isRValueReference(type)) {
                    return true;
                }

                if (!Type.isConst(type, 1)) {
                    result.second().first(ctorNames[0]);
                    result.second().second(index);
                    return true;
                }
            }

            return false;
        }, cache);

        result.first(violatesViaFunctionCall || violatesViaConstructorCall || violatesViaInitializerList);
        return result;
    }

    public static boolean isReturnedAsCopy(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(IASTReturnStatement.class, name, (IASTReturnStatement statement, IASTName reference) -> {
            IType returnType;
            if (Relation.isDescendendOf(ICPPASTLambdaExpression.class, reference)) {
                ICPPASTLambdaExpression lambdaExpression = Relation.getAncestorOf(ICPPASTLambdaExpression.class, reference);
                CPPClosureType lambdaType = Cast.as(CPPClosureType.class, lambdaExpression.getExpressionType());
                returnType = lambdaType.getFunctionCallOperator().getType().getReturnType();
            } else {
                ICPPASTFunctionDefinition function = Relation.getAncestorOf(ICPPASTFunctionDefinition.class, statement);
                if (function == null) {
                    return false;
                }

                ICPPFunction functionBinding = Cast.as(ICPPFunction.class, function.getDeclarator().getName().resolveBinding());
                if (functionBinding == null) {
                    return false;
                }
                returnType = functionBinding.getType().getReturnType();
            }
            if (!(SemanticUtil.getSimplifiedType(returnType) instanceof ICPPBasicType)) {
                return !(Type.isReference(returnType) || Type.isPointer(returnType));
            }

            return false;
        }, cache);
    }

    public static boolean isReturnedAsPointerToNonConst(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(IASTReturnStatement.class, name, (IASTReturnStatement statement, IASTName reference) -> {
            ICPPASTExpression returnValue = Cast.as(ICPPASTExpression.class, statement.getReturnValue());
            ICPPASTName resultingName = Expression.getResultingName(returnValue);

            if (resultingName != null) {
                if (!resultingName.resolveBinding().equals(name.resolveBinding()) || !Node.isUsedToTakeAddressOf(resultingName)) {
                    return false;
                }

                ICPPASTFunctionDefinition functionDefinition;
                if ((functionDefinition = Relation.getAncestorOf(ICPPASTFunctionDefinition.class, statement)) == null) {
                    return false;
                }

                ICPPFunction function;
                if ((function = Cast.as(ICPPFunction.class, functionDefinition.getDeclarator().getName().resolveBinding())) == null) {
                    return false;
                }

                return !Type.isConst(function.getType().getReturnType(), 1);
            }

            return false;
        }, cache);
    }

    public static boolean isReturnedAsReferenceToNonConst(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(IASTReturnStatement.class, name, (IASTReturnStatement statement, IASTName reference) -> {
            ICPPASTExpression returnValue = Cast.as(ICPPASTExpression.class, statement.getReturnValue());
            ICPPASTName resultingName = Expression.getResultingName(returnValue);

            if (resultingName != null) {
                if (!resultingName.resolveBinding().equals(name.resolveBinding())) {
                    return false;
                }

                ICPPASTFunctionDefinition functionDefinition;
                if ((functionDefinition = Relation.getAncestorOf(ICPPASTFunctionDefinition.class, statement)) == null) {
                    return false;
                }

                ICPPFunction function;
                if ((function = Cast.as(ICPPFunction.class, functionDefinition.getDeclarator().getName().resolveBinding())) == null) {
                    return false;
                }

                return Type.isReference(function.getType().getReturnType()) && !Type.isConst(function.getType().getReturnType(), 1);
            }

            return false;
        }, cache);
    }

    public static boolean isUsedToBindLValueReferenceToNonConst(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(IASTInitializer.class, name, (IASTInitializer initializer, IASTName reference) -> {
            ICPPASTDeclarator declarator = Relation.getAncestorOf(ICPPASTDeclarator.class, initializer);

            if (declarator == null) {
                return false;
            }

            IASTPointerOperator[] pointerOperators = declarator.getPointerOperators();

            if (pointerOperators == null || pointerOperators.length != 1) {
                return false;
            }

            ICPPASTReferenceOperator referenceOperator = Cast.as(ICPPASTReferenceOperator.class, pointerOperators[0]);

            if (referenceOperator != null && referenceOperator.isRValueReference()) {
                return false;
            }

            IASTSimpleDeclaration declaration = Relation.getAncestorOf(IASTSimpleDeclaration.class, declarator);

            return !declaration.getDeclSpecifier().isConst();
        }, cache);
    }

    public static boolean nonConstMemberIsModified(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTFieldReference.class, name, (ICPPASTFieldReference expression, IASTName reference) -> {
            if (Cast.as(ICPPField.class, expression.getFieldName().resolveBinding()) == null) {
                return false;
            }
            ICPPASTName fieldName = Cast.as(ICPPASTName.class, expression.getFieldName());

            ICPPASTBinaryExpression binExp = Relation.getAncestorOf(ICPPASTBinaryExpression.class, expression);
            if (binExp != null) {

                if (fieldName != null && ((Expression.isLeftHandOperandOf(fieldName, binExp) && Expression.isModifyingOperation(binExp,
                        OperatorSide.LeftHandSide)) || (Expression.isRightHandOperandOf(fieldName, binExp) && Expression.isModifyingOperation(binExp,
                                OperatorSide.RightHandSide)))) {
                    return true;
                }
            }

            ICPPASTUnaryExpression unaExp = Relation.getAncestorOf(ICPPASTUnaryExpression.class, expression);
            if (unaExp != null) {
                if (Expression.isModifyingOperation(unaExp)) {
                    return true;
                }
            }

            if (fieldName != null && (isPassedAsLValueReferenceToNonConst(fieldName, cache).first() || addressIsAssignedToPointerToNonConst(fieldName,
                    cache) || addressIsPassedAsReferenceToPointerToNonConst(fieldName, cache) || addressIsUsedToBindReferenceToPointerToNonConst(
                            fieldName, cache) || addressIsPassedAsPointerToNonConst(fieldName, cache).first())) {
                return true;
            }

            ICPPASTFieldReference parentAccess = Relation.getAncestorOf(ICPPASTFieldReference.class, expression.getParent());
            if (parentAccess != null) {
                IASTName parentFieldName = parentAccess.getFieldName();
                IBinding fieldBinding = parentFieldName.resolveBinding();

                if (fieldBinding instanceof ICPPMethod) {
                    return !((ICPPMethod) fieldBinding).getType().isConst();
                } else if (fieldBinding instanceof ICPPVariable) {
                    ICPPVariable memberVariable = (ICPPVariable) fieldBinding;
                    if (memberVariable instanceof CPPVariable) {
                        IASTNode definition = ((CPPVariable) memberVariable).getDefinition();
                        if (!Type.isConst(memberVariable.getType(), 0)) {
                            return nonConstMemberIsModified((ICPPASTName) definition, cache);
                        }
                    }
                }
            }

            return false;
        }, cache);
    }

    public static boolean nonConstMemberFunctionCalledOn(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTFieldReference.class, name, (ICPPASTFieldReference expression, IASTName reference) -> {
            if (!(expression.getParent() instanceof IASTFunctionCallExpression)) {
                return false;
            }

            IBinding fieldBinding = expression.getFieldName().getBinding();
            ICPPMethod memberFunction = Cast.as(ICPPMethod.class, fieldBinding);

            if (memberFunction == null) {
                return false;
            }

            return !memberFunction.getType().isConst();
        }, cache) || Node.anyOfDescendingFrom(ICPPASTFunctionCallExpression.class, name, (ICPPASTFunctionCallExpression expression,
                IASTName reference) -> {

            IASTName functionName = Relation.getDescendendOf(IASTName.class, expression.getFunctionNameExpression());
            if (functionName == reference) {
                ICPPFunction functionCallOperator = expression.getOverload();
                if (functionCallOperator != null) {
                    return !functionCallOperator.getType().isConst();
                } else {
                    // If we encounter a template function, we
                    // bail out
                    ICPPEvaluation evaluation = expression.getEvaluation();
                    return evaluation.getTemplateDefinition() != null;
                }
            }

            return false;
        }, cache);
    }

    public static boolean isOperandInModifyingSubscriptExpressions(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTArraySubscriptExpression.class, name, (expression, reference) -> {
            ICPPASTExpression array = expression.getArrayExpression();
            ICPPASTName arrayName = Expression.getResultingName(array);
            if (arrayName == null || name == null || !arrayName.resolveBinding().equals(name.resolveBinding())) {
                return false;
            }

            IASTImplicitName[] operators = expression.getImplicitNames();
            if (operators.length == 0) {
                return false;
            }

            ICPPMethod operator = Cast.as(ICPPMethod.class, operators[0].resolveBinding());
            if (operator == null) {
                return false;
            }

            return !operator.getType().isConst();
        }, cache);
    }

    public static boolean isUsedAsModifiableRangeBasedForInitializer(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTRangeBasedForStatement.class, name, (rangeFor, reference) -> {
            return Optional.of(reference)
                .filter(r -> Relation.getDescendendsOf(IASTName.class, rangeFor.getInitializerClause()).contains(r))
                .map(r -> Relation.getDescendendOf(ICPPASTDeclarator.class, rangeFor.getDeclaration()))
                .map(ICPPASTDeclarator::getName)
                .map(IASTName::resolveBinding)
                .filter(b -> b instanceof ICPPVariable)
                .map(b -> (ICPPVariable)b)
                .map(ICPPVariable::getType)
                .map(t -> {
                    return Type.isReference(t) && !Type.isConst(t, 1);
                }).orElse(false);
        }, cache);
    }
}
