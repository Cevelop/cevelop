package com.cevelop.constificator.core.deciders.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;

import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.semantic.Expression;
import com.cevelop.constificator.core.util.semantic.Expression.OperatorSide;
import com.cevelop.constificator.core.util.semantic.Type;
import com.cevelop.constificator.core.util.structural.Node;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.core.util.type.ReferenceWrapper;


public class Arrays {

    public static boolean elementIsModifiedInUnaryExpression(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTUnaryExpression.class, name, (ICPPASTUnaryExpression expression, IASTName reference) -> {

            ICPPVariable variable = Cast.as(ICPPVariable.class, name.getBinding());
            if (variable == null || !variable.equals(reference.getBinding())) {
                return false;
            }

            ReferenceWrapper<ICPPASTExpression> breaking = new ReferenceWrapper<>();
            if (Expression.isArrayElementAccess(reference, breaking)) {
                ICPPASTUnaryExpression unary = Cast.as(ICPPASTUnaryExpression.class, breaking.get());
                return unary != null && Expression.isModifyingOperation(unary);
            }

            return false;
        }, cache);
    }

    public static boolean isPassedToFunctionTakingArrayOfNonConst(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTFunctionCallExpression.class, name, (ICPPASTFunctionCallExpression call, IASTName reference) -> {
            ICPPFunction function = Node.getBindingForFunction(call.getFunctionNameExpression());
            if (function == null) {
                return false;
            }

            ICPPVariable argument = Cast.as(ICPPVariable.class, reference.resolveBinding());
            if (argument == null) {
                return false;
            }

            List<Integer> occurences = new ArrayList<>();
            IASTInitializerClause[] arguments = call.getArguments();
            for (int argumentIndex = 0; argumentIndex < arguments.length; ++argumentIndex) {
                ICPPASTName argumentName = Expression.getResultingName((ICPPASTInitializerClause) arguments[argumentIndex]);
                if (argumentName != null && argumentName.resolveBinding().equals(name.getBinding())) {
                    occurences.add(argumentIndex);
                }
            }

            ICPPParameter[] parameters = function.getParameters();
            for (Integer parameterIndex : occurences) {
                ICPPParameter parameter = parameters[parameterIndex];
                final int parameterDimension = Type.arrayDimension(parameter.getType());

                if (!Type.isConst(parameter.getType(), parameterDimension)) {
                    return true;
                }
            }

            return false;
        }, cache);
    }

    public static boolean nonConstMemberFunctionCalledOnElement(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTFieldReference.class, name, (ICPPASTFieldReference fieldReference, IASTName reference) -> {
            if (!Expression.isArrayElementAccess(reference)) {
                return false;
            }

            ICPPMethod memberFunction = null;
            if ((memberFunction = Cast.as(ICPPMethod.class, fieldReference.getFieldName().resolveBinding())) == null) {
                return false;
            }

            return !memberFunction.getType().isConst();
        }, cache);
    }

    public static boolean nonConstMemberModifiedOnElement(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTFieldReference.class, name, (ICPPASTFieldReference fieldReference, IASTName reference) -> {
            if (!Expression.isArrayElementAccess(reference)) {
                return false;
            }

            if (Cast.as(ICPPField.class, fieldReference.getFieldName().resolveBinding()) == null) {
                return false;
            }

            ICPPASTUnaryExpression unary = Relation.getAncestorOf(ICPPASTUnaryExpression.class, fieldReference);
            while (unary != null) {
                if (Expression.isModifyingOperation(unary)) {
                    return true;
                }
                unary = Relation.getAncestorOf(ICPPASTUnaryExpression.class, unary.getParent());
            }

            ICPPASTBinaryExpression binary = Relation.getAncestorOf(ICPPASTBinaryExpression.class, fieldReference);
            while (binary != null) {
                if ((Expression.isLeftHandOperandOf(name, binary) && Expression.isModifyingOperation(binary, OperatorSide.LeftHandSide)) ||
                    (Expression.isRightHandOperandOf(name, binary) && Expression.isModifyingOperation(binary, OperatorSide.RightHandSide))) {
                    return true;
                }

                binary = Relation.getAncestorOf(ICPPASTBinaryExpression.class, binary.getParent());
            }

            return false;
        }, cache);
    }

}
