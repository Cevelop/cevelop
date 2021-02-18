package com.cevelop.constificator.core.deciders.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTImplicitNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBasicType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;
import org.eclipse.cdt.internal.core.dom.parser.ASTQueries;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.functional.BinaryPredicate;
import com.cevelop.constificator.core.util.functional.TernaryPredicate;
import com.cevelop.constificator.core.util.semantic.Expression;
import com.cevelop.constificator.core.util.semantic.Expression.OperatorSide;
import com.cevelop.constificator.core.util.semantic.Function;
import com.cevelop.constificator.core.util.semantic.Type;
import com.cevelop.constificator.core.util.semantic.Variable;
import com.cevelop.constificator.core.util.structural.Node;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.core.util.type.ReferenceWrapper;


@SuppressWarnings("restriction")
public class PointerVariables {

    public static boolean addressIsAssignedToPointerToNonConstPointer(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTBinaryExpression.class, name, (ICPPASTBinaryExpression expr, IASTName reference) -> {
            while (expr != null && expr.getOperator() != IASTBinaryExpression.op_assign) {
                expr = Relation.getAncestorOf(ICPPASTBinaryExpression.class, expr.getParent());
            }

            if (expr == null) {
                return false;
            }

            ICPPASTName lhsName;
            ICPPASTName rhsName;
            if ((lhsName = Relation.getDescendendOf(ICPPASTName.class, expr.getOperand1())) == null || (rhsName = Relation.getDescendendOf(
                    ICPPASTName.class, expr.getOperand2())) == null) {
                return false;
            }

            ICPPVariable lhs;
            ICPPVariable rhs;
            if ((lhs = Cast.as(ICPPVariable.class, lhsName.resolveBinding())) == null || (rhs = Cast.as(ICPPVariable.class, rhsName
                    .resolveBinding())) == null) {
                return false;
            }

            if (!rhs.equals(name.resolveBinding())) {
                return false;
            }

            IType lhsType = lhs.getType();
            IType rhsType = rhs.getType();

            if (Type.pointerLevels(lhsType) != Type.pointerLevels(rhsType) + 1) {
                return false;
            }

            return !Type.isConst(lhsType, 1);
        }, cache);
    }

    public static boolean addressIsPassedAsPointerToNonConstPointer(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTFunctionCallExpression.class, name, (ICPPASTFunctionCallExpression expression, IASTName reference) -> {
            List<Integer> argumentIndexes = Function.getArgumentIndicesFor(expression.getArguments(), name, (n) -> Node.isUsedToTakeAddressOf(n));

            ICPPFunction function;
            if ((function = Node.getBindingForFunction(expression.getFunctionNameExpression())) == null) {
                return false;
            }

            ICPPParameter[] parameters = function.getParameters();

            for (int index : argumentIndexes) {
                ICPPParameter parameter = parameters[index];

                if (!(parameter.getType() instanceof IPointerType)) {
                    continue;
                }

                IPointerType parameterType = (IPointerType) parameter.getType();
                IPointerType pointeeType = Cast.as(IPointerType.class, parameterType.getType());

                if (pointeeType != null && !pointeeType.isConst()) {
                    return true;
                }
            }

            return false;
        }, cache);
    }

    public static boolean addressIsPassedAsReferenceToPointerToNonConstPointer(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTFunctionCallExpression.class, name, (ICPPASTFunctionCallExpression ancestor, IASTName reference) -> {
            ICPPASTFunctionCallExpression expression = ancestor;

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

            IASTName functionName = null;
            IASTExpression functionNameExpression = expression.getFunctionNameExpression();
            if (functionNameExpression instanceof IASTFieldReference) {
                functionName = ((IASTFieldReference) functionNameExpression).getFieldName();
            } else if (functionNameExpression instanceof IASTIdExpression) {
                functionName = ((IASTIdExpression) functionNameExpression).getName();
            }

            IBinding binding = (functionName != null) ? functionName.resolveBinding() : null;
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
                    IPointerType pointeeType = Cast.as(IPointerType.class, referencedType.getType());

                    if (pointeeType == null || !pointeeType.isConst()) {
                        return true;
                    }
                }
            }

            return false;
        }, cache);
    }

    public static boolean addressIsUsedToBindReferenceToPointerToNonConstPointer(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(IASTInitializer.class, name, (IASTInitializer ancestor, IASTName reference) -> {
            IASTInitializer initializer = ancestor;
            ICPPASTDeclarator declarator = Relation.getAncestorOf(ICPPASTDeclarator.class, initializer);
            IASTPointerOperator[] pointerOperators = declarator != null ? declarator.getPointerOperators() : null;
            ICPPASTUnaryExpression unary = Relation.getAncestorOf(ICPPASTUnaryExpression.class, reference);

            return unary != null //
            && unary.getOperator() == IASTUnaryExpression.op_amper //
            && pointerOperators != null //
            && pointerOperators.length > 2 //
            && Node.declaresLValueReferenceToPointer(declarator) //
            && Cast.as(IASTPointer.class, pointerOperators[pointerOperators.length - 3]) != null //
            && !Cast.as(IASTPointer.class, pointerOperators[pointerOperators.length - 3]).isConst();

        }, cache);
    }

    public static boolean addressIsUsedToInitializePointerToNonConstPointer(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(IASTInitializer.class, name, (IASTInitializer init, IASTName reference) -> {
            if (Node.isUsedToTakeAddressOf(reference)) {
                ICPPASTDeclarator declarator;
                if ((declarator = Relation.getAncestorOf(ICPPASTDeclarator.class, init)) == null) {
                    return false;
                }

                ICPPVariable lhs;

                if ((lhs = Cast.as(ICPPVariable.class, declarator.getName().resolveBinding())) == null) {
                    return false;
                }

                return !Type.isConst(lhs.getType(), 1);
            }

            return false;
        }, cache);
    }

    public static boolean isAssignedToPointerToNonConst(ICPPASTName name, int pointerLevel, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTBinaryExpression.class, name, (ICPPASTBinaryExpression expr, IASTName reference) -> {
            while (expr != null && expr.getOperator() != IASTBinaryExpression.op_assign) {
                expr = Relation.getAncestorOf(ICPPASTBinaryExpression.class, expr.getParent());
            }

            if (expr == null) {
                return false;
            }

            ICPPASTName lhsName;
            ICPPASTName rhsName;
            if ((lhsName = Relation.getDescendendOf(ICPPASTName.class, expr.getOperand1())) == null || (rhsName = Relation.getDescendendOf(
                    ICPPASTName.class, expr.getOperand2())) == null) {
                return false;
            }

            ICPPVariable lhs;
            ICPPVariable rhs;
            if ((lhs = Cast.as(ICPPVariable.class, lhsName.resolveBinding())) == null || (rhs = Cast.as(ICPPVariable.class, rhsName
                    .resolveBinding())) == null) {
                return false;
            }

            if (!rhs.equals(name.resolveBinding())) {
                return false;
            }

            IType lhsType = lhs.getType();
            IType rhsType = rhs.getType();

            if (!Type.isPointer(lhsType)) {
                return false;
            }

            if (Type.pointerLevels(lhsType) == Type.pointerLevels(rhsType)) {
                return !Type.isConst(lhsType, pointerLevel);
            }

            return !Type.isConst(lhsType, pointerLevel + 1);
        }, cache);
    }

    public static boolean isInitializedUsingLessConstPointer(ICPPASTName name, int pointerLevel, ASTRewriteCache cache) {
        ICPPASTDeclarator decl;
        if ((decl = Relation.getAncestorOf(ICPPASTDeclarator.class, name)) == null) {
            return false;
        }

        IASTInitializer init;
        if ((init = decl.getInitializer()) == null) {
            return false;
        }

        IASTNode[] children;
        if ((children = init.getChildren()).length == 0) {
            return false;
        }

        ICPPASTName other;
        if ((other = Relation.getDescendendOf(ICPPASTName.class, children[0])) == null) {
            return false;
        }

        ICPPVariable otherPtr;
        if ((otherPtr = Cast.as(ICPPVariable.class, other.resolveBinding())) == null) {
            return false;
        }

        IBinding ptrBinding = name.resolveBinding();
        IType ptrType = ptrBinding instanceof ICPPVariable ? ((ICPPVariable) ptrBinding).getType() : ((ICPPParameter) ptrBinding).getType();

        if (!Type.isConst(otherPtr.getType(), pointerLevel) && pointerLevel > 1) {
            return !Type.isConst(ptrType, pointerLevel - 1);
        }

        return false;
    }

    public static boolean isPassedAsLValueReferenceToNonConstPointer(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTFunctionCallExpression.class, name, (ICPPASTFunctionCallExpression ancestor, IASTName reference) -> {
            ICPPASTFunctionCallExpression expression = ancestor;

            if (Relation.isDescendendOf(ICPPASTUnaryExpression.class, reference) || Relation.isDescendendOf(ICPPASTFieldReference.class, reference)) {
                return false;
            }

            List<Integer> indices = Function.getArgumentIndicesFor(expression.getArguments(), name, (n) -> {
                return Expression.isDereferencedNTimes(n, 0);
            });

            ICPPFunction function = Node.getBindingForFunction(expression.getFunctionNameExpression());

            if (function != null) {
                ICPPParameter[] parameters = function.getParameters();

                for (int index : indices) {
                    ICPPParameter parameter = parameters[index];

                    if (!(parameter.getType() instanceof ICPPReferenceType)) {
                        continue;
                    }

                    if (((ICPPReferenceType) parameter.getType()).isRValueReference()) {
                        continue;
                    }

                    ICPPReferenceType parameterType = (ICPPReferenceType) parameter.getType();
                    IPointerType pointerType = Cast.as(IPointerType.class, parameterType.getType());

                    if (pointerType != null && !pointerType.isConst()) {
                        return true;
                    }
                }
            }

            return false;
        }, cache);
    }

    public static boolean isPassedToFunctionTakingPointerToNonConst(ICPPASTName name, int pointerLevel, ASTRewriteCache cache) {
        TernaryPredicate<IASTInitializerClause[], ICPPParameter[], Integer> passedAsPointerToNonConst = (IASTInitializerClause[] arguments,
                ICPPParameter[] parameters, Integer pointerLevelWithOffset) -> {
            List<Integer> indices = Function.getArgumentIndicesFor(arguments, name, null);
            for (int index : indices) {
                if (index >= parameters.length) {
                    break;
                }

                ICPPParameter parameter = parameters[index];
                if (!(parameter.getType() instanceof IPointerType)) {
                    continue;
                }

                return !Type.isConst(parameter.getType(), pointerLevelWithOffset);
            }
            return false;
        };

        Boolean violatesViaFunctionCall = Node.anyOfDescendingFrom(ICPPASTFunctionCallExpression.class, name, (ICPPASTFunctionCallExpression ancestor,
                IASTName reference) -> {
            ICPPASTFunctionCallExpression expression = ancestor;

            if (Relation.isDescendendOf(ICPPASTFieldReference.class, reference)) {
                return false;
            }

            ICPPFunction function;
            IASTImplicitName[] ctorNames = expression.getImplicitNames();
            if (ctorNames.length > 0) {
                ICPPConstructor ctor = Cast.as(ICPPConstructor.class, ctorNames[0].getBinding());
                function = ctor;
            } else {
                function = Node.getBindingForFunction(expression.getFunctionNameExpression());
            }

            int pointerLevelWithOffset = pointerLevel + (Node.isUsedToTakeAddressOf(reference) ? 1 : 0);
            return function != null && passedAsPointerToNonConst.holdsFor(expression.getArguments(), function.getParameters(),
                    pointerLevelWithOffset);
        }, cache);

        Boolean violatesViaConstructorCall = Node.anyOfDescendingFrom(IASTInitializer.class, name, (IASTInitializer initializer,
                IASTName reference) -> {

            IASTInitializerClause[] arguments;
            if (initializer instanceof ICPPASTConstructorInitializer) {
                ICPPASTConstructorInitializer constructorInitializer = (ICPPASTConstructorInitializer) initializer;
                arguments = constructorInitializer.getArguments();
            } else if (initializer instanceof IASTInitializerList) {
                IASTInitializerList initializerList = (IASTInitializerList) initializer;
                arguments = initializerList.getClauses();
            } else {
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
            int pointerLevelWithOffset = pointerLevel + (Node.isUsedToTakeAddressOf(reference) ? 1 : 0);
            return ctor != null && passedAsPointerToNonConst.holdsFor(arguments, ctor.getParameters(), pointerLevelWithOffset);
        }, cache);

        return violatesViaFunctionCall || violatesViaConstructorCall;
    }

    public static boolean isPassedToPlacementNew(ICPPASTName name, int pointerLevel, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTNewExpression.class, name, (ICPPASTNewExpression ancestor, IASTName reference) -> {
            IASTInitializerClause[] placementArguments = ancestor.getPlacementArguments();
            return placementArguments != null && Arrays.stream(placementArguments).anyMatch(placementArgument -> ASTQueries.isAncestorOf(
                    placementArgument, reference));
        }, cache);
    }

    public static boolean isPassedToFunctionTakingReferenceToPointerToNonConst(ICPPASTName name, int pointerLevel, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTFunctionCallExpression.class, name, (ICPPASTFunctionCallExpression call, IASTName reference) -> {
            if (!Expression.isDereferencedNTimes(reference, 0)) {
                return false;
            }

            ArrayList<Integer> argumentIndexes = new ArrayList<>();
            IASTInitializerClause[] arguments = call.getArguments();

            for (int i = 0; i < arguments.length; ++i) {
                IASTIdExpression idExpression = Relation.getDescendendOf(IASTIdExpression.class, arguments[i]);

                if (idExpression != null && idExpression.getName().getBinding().equals(reference.getBinding())) {
                    argumentIndexes.add(i);
                }
            }

            ICPPFunction function = Node.getBindingForFunction(call.getFunctionNameExpression());

            if (function != null) {
                ICPPParameter[] parameters = function.getParameters();

                for (int index : argumentIndexes) {
                    ICPPParameter parameter = parameters[index];
                    IType parameterType = parameter.getType();

                    return Type.isReference(parameterType) && !Type.isConst(parameterType, pointerLevel + 1);
                }
            }
            return false;
        }, cache);
    }

    public static boolean isUsedToBindLValueReferenceToNonConstPointer(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(IASTInitializer.class, name, (IASTInitializer ancestor, IASTName reference) -> {
            IASTInitializer initializer = ancestor;
            ICPPASTDeclarator declarator = Relation.getAncestorOf(ICPPASTDeclarator.class, initializer);
            IASTPointerOperator[] pointerOperators = declarator != null ? declarator.getPointerOperators() : null;

            return pointerOperators != null && pointerOperators.length >= 2 && Node.declaresLValueReferenceToPointer(declarator) && Cast.as(
                    IASTPointer.class, pointerOperators[pointerOperators.length - 2]) != null && !Cast.as(IASTPointer.class,
                            pointerOperators[pointerOperators.length - 2]).isConst();
        }, cache);
    }

    public static boolean isUsedToBindReferenceToPointerToNonConst(ICPPASTName name, int pointerLevel, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(IASTInitializer.class, name, (IASTInitializer init, IASTName reference) -> {
            if (!Expression.isDereferencedNTimes(reference, 0)) {
                return false;
            }

            ICPPASTDeclarator declarator = Relation.getAncestorOf(ICPPASTDeclarator.class, init);
            if (declarator == null) {
                return false;
            }
            ICPPVariable lhs;

            if ((lhs = Cast.as(ICPPVariable.class, declarator.getName().resolveBinding())) == null) {
                return false;
            }

            return Type.isReference(lhs.getType()) && !Type.isConst(lhs.getType(), pointerLevel + 1);
        }, cache);
    }

    public static boolean isUsedToInitializePointerToNonConst(ICPPASTName name, int pointerLevel, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(IASTInitializer.class, name, (IASTInitializer init, IASTName reference) -> {
            ICPPVariable lhs = Variable.getInitializedVariable(init);

            if (lhs == null || !Type.areSameTypeIgnoringConst(lhs.getType(), ((ICPPVariable) name.resolveBinding()).getType())) {
                return false;
            }

            return !Type.isConst(lhs.getType(), pointerLevel);
        }, cache);
    }

    public static boolean nonConstMemberAccessedOnPointee(ICPPASTName name, int pointerLevel, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTFieldReference.class, name, (ICPPASTFieldReference expression, IASTName reference) -> {
            ICPPASTName owner = Expression.getResultingName(expression.getFieldOwner());

            if (owner != null && owner.equals(reference)) {
                IASTName field = expression.getFieldName();

                if (field != null) {
                    ICPPMember member = Cast.as(ICPPMember.class, field.resolveBinding());

                    try {
                        return !Type.isConst(member.getType(), 0);
                    } catch (Exception e) {}
                }
            }

            return false;
        }, cache);
    }

    public static boolean pointeeIsLeftHandSideInModifyingBinaryExpression(ICPPASTName name, int dereferenceLevel, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTBinaryExpression.class, name, (ICPPASTBinaryExpression ancestor, IASTName reference) -> {

            ReferenceWrapper<ICPPASTExpression> breakingExpression = new ReferenceWrapper<>();
            if (Expression.isDereferencedNTimes(reference, dereferenceLevel, breakingExpression)) {
                ICPPASTBinaryExpression found = Cast.as(ICPPASTBinaryExpression.class, breakingExpression.get());

                if (found == null) {
                    return false;
                }

                return (Expression.isLeftHandOperandOf((ICPPASTName) reference, found) && Expression.isModifyingOperation(found,
                        OperatorSide.LeftHandSide)) || (Expression.isRightHandOperandOf((ICPPASTName) reference, found) && Expression
                                .isModifyingOperation(found, OperatorSide.RightHandSide));
            }

            return false;
        }, cache);
    }

    public static boolean pointeeIsLeftHandSideInModifyingUnaryExpression(ICPPASTName name, int dereferenceLevel, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTUnaryExpression.class, name, (ICPPASTUnaryExpression ancestor, IASTName reference) -> {

            ReferenceWrapper<ICPPASTExpression> breakingExpression = new ReferenceWrapper<>();
            if (Expression.isDereferencedNTimes(reference, dereferenceLevel, breakingExpression)) {
                ICPPASTUnaryExpression found = Cast.as(ICPPASTUnaryExpression.class, breakingExpression.get());
                return found != null && Expression.isModifyingOperation(found);
            }

            return false;
        }, cache);
    }

    public static boolean pointeeIsPassedAsReferenceToNonConst(ICPPASTName name, int dereferenceLevel, ASTRewriteCache cache) {
        BinaryPredicate<IASTInitializerClause[], ICPPParameter[]> passedAsReferenceToNonConst = (IASTInitializerClause[] arguments,
                ICPPParameter[] parameters) -> {
            List<Integer> indices = Function.getArgumentIndicesFor(arguments, name, null);

            if (indices.isEmpty()) {
                return false;
            }

            if (parameters.length <= indices.get(indices.size() - 1)) {
                return false;
            }

            for (int index : indices) {
                ICPPParameter parameter = parameters[index];

                if (!(parameter.getType() instanceof ICPPReferenceType)) {
                    continue;
                }

                ICPPReferenceType topLevelParameterType = (ICPPReferenceType) parameter.getType();
                IType referencedType = SemanticUtil.getSimplifiedType(topLevelParameterType.getType());

                if (referencedType instanceof IPointerType) {
                    return !((IPointerType) referencedType).isConst();
                } else if (referencedType instanceof IQualifierType) {
                    return !((IQualifierType) referencedType).isConst();
                } else if (referencedType instanceof ICPPBasicType) {
                    return true;
                } else if (referencedType instanceof ICPPClassType) {
                    return true;
                }
            }
            return false;
        };

        Boolean violatesViaFunctionCall = Node.anyOfDescendingFrom(ICPPASTFunctionCallExpression.class, name, (ICPPASTFunctionCallExpression ancestor,
                IASTName reference) -> {
            ICPPASTFunctionCallExpression expression = ancestor;

            if (Relation.isDescendendOf(ICPPASTFieldReference.class, reference)) {
                return false;
            }

            if (!Expression.isDereferencedNTimes(reference, dereferenceLevel)) {
                return false;
            }

            ICPPFunction function;
            IASTImplicitName[] ctorNames = expression.getImplicitNames();
            if (ctorNames.length > 0) {
                ICPPConstructor ctor = Cast.as(ICPPConstructor.class, ctorNames[0].getBinding());
                function = ctor;
            } else {
                function = Node.getBindingForFunction(expression.getFunctionNameExpression());
            }
            return function != null && passedAsReferenceToNonConst.holdsFor(expression.getArguments(), function.getParameters());
        }, cache);

        Boolean violatesViaConstructorCall = Node.anyOfDescendingFrom(IASTInitializer.class, name, (IASTInitializer initializer,
                IASTName reference) -> {

            if (!Expression.isDereferencedNTimes(reference, dereferenceLevel)) {
                return false;
            }

            IASTInitializerClause[] arguments;
            if (initializer instanceof ICPPASTConstructorInitializer) {
                ICPPASTConstructorInitializer constructorInitializer = (ICPPASTConstructorInitializer) initializer;
                arguments = constructorInitializer.getArguments();
            } else if (initializer instanceof IASTInitializerList) {
                IASTInitializerList initializerList = (IASTInitializerList) initializer;
                arguments = initializerList.getClauses();
            } else {
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
            return ctor != null && passedAsReferenceToNonConst.holdsFor(arguments, ctor.getParameters());
        }, cache);

        return violatesViaFunctionCall || violatesViaConstructorCall;
    }

    public static boolean pointeeIsReturnedAsReferenceToNonConst(ICPPASTName name, int pointerLevel, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(IASTReturnStatement.class, name, (IASTReturnStatement statement, IASTName reference) -> {

            if (Expression.isDereferencedNTimes(reference, pointerLevel)) {
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

            }
            return false;
        }, cache);
    }

    public static boolean pointeeIsUsedToBindReferenceToNonConst(ICPPASTName name, int dereferenceLevel, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(IASTInitializer.class, name, (IASTInitializer initializer, IASTName reference) -> {
            ICPPVariable variable = Variable.getInitializedVariable(initializer);

            if (variable != null && Type.isReference(variable.getType())) {
                return !Type.isConst(variable.getType(), dereferenceLevel);
            }

            return false;
        }, cache);
    }

}
