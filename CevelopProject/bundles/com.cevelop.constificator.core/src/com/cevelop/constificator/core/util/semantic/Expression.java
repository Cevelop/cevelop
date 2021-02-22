package com.cevelop.constificator.core.util.semantic;

import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCastExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Arrays;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.core.util.type.ReferenceWrapper;


/**
 * Utilities for determining the semantic properties of C++ expressions.
 *
 * @author Felix Morgner
 */
@SuppressWarnings("restriction")
public class Expression {

   //@formatter:off
    private static final Integer[] MODIFYING_BINARY_OPERATORS = {
            ICPPASTBinaryExpression.op_assign,
            ICPPASTBinaryExpression.op_binaryAndAssign,
            ICPPASTBinaryExpression.op_binaryOrAssign,
            ICPPASTBinaryExpression.op_binaryXorAssign,
            ICPPASTBinaryExpression.op_divideAssign,
            ICPPASTBinaryExpression.op_minusAssign,
            ICPPASTBinaryExpression.op_moduloAssign,
            ICPPASTBinaryExpression.op_multiplyAssign,
            ICPPASTBinaryExpression.op_plusAssign,
            ICPPASTBinaryExpression.op_shiftLeftAssign,
            ICPPASTBinaryExpression.op_shiftRightAssign,
    };

    private static final Integer[] MODIFYING_UNARY_OPERATORS = {
            ICPPASTUnaryExpression.op_prefixIncr,
            ICPPASTUnaryExpression.op_prefixDecr,
            ICPPASTUnaryExpression.op_postFixIncr,
            ICPPASTUnaryExpression.op_postFixDecr,
    };

    public enum OperatorSide {
        LeftHandSide,
        RightHandSide,
    }
    //@formatter:on

    /**
     * Retrieve the name of the entity the given initializer-clause refers to.
     *
     * @param clause
     * The initializer-clause to inspect
     * @return The name of the referenced entity, or null if no named entity is
     * referenced by the initializer-clause.
     */
    public static ICPPASTName getResultingName(ICPPASTInitializerClause clause) {

        IASTExpressionList list;
        if ((list = Relation.getDescendendOf(IASTExpressionList.class, clause)) != null) {
            return Expression.getResultingName((ICPPASTInitializerClause) list.getExpressions()[list.getExpressions().length - 1]);
        } else if (clause instanceof IASTIdExpression) {
            return Cast.as(ICPPASTName.class, ((IASTIdExpression) clause).getName());
        } else if (clause instanceof IASTUnaryExpression) {
            return Expression.getResultingName((ICPPASTInitializerClause) ((IASTUnaryExpression) clause).getOperand());
        } else if (clause instanceof ICPPASTBinaryExpression) {
            return Expression.getResultingName((ICPPASTInitializerClause) ((ICPPASTBinaryExpression) clause).getOperand1());
        } else if (clause instanceof ICPPASTCastExpression) {
            return Expression.getResultingName((ICPPASTInitializerClause) ((ICPPASTCastExpression) clause).getOperand());
        } else if (clause instanceof ICPPASTFieldReference) {
            return (ICPPASTName) ((ICPPASTFieldReference) clause).getFieldName();
        } else if (clause instanceof ICPPASTArraySubscriptExpression) {
            return Expression.getResultingName(((ICPPASTArraySubscriptExpression) clause).getArrayExpression());
        }

        return null;
    }

    /**
     * Checks if any elements of a set of overloaded operators takes a reference
     * to non <code>const</code>-qualified type as its left-hand side parameter.
     *
     * @param names
     * An array of implicit names, like the one returned by
     * {@link ICPPASTUnaryExpression#getImplicitNames()}.
     * @return <code>true</code> iff. any of the overloaded operators in
     * <code>names</code> takes it left-hand side parameter via a
     * reference to non <code>const</code>-qualified type.
     */
    private static boolean hasAnyOperatorNonConstLhsParameter(IASTImplicitName... names) {
        return java.util.Arrays.stream(names).anyMatch(name -> {
            ICPPFunction function = Cast.as(ICPPFunction.class, name.getBinding());

            if (function == null) {
                return false;
            }

            if (function instanceof ICPPMethod) {
                return !MemberFunction.isConst(function);
            } else if (function.getParameters().length >= 1) {
                return (Type.isReference(function.getParameters()[0].getType()) && !Function.nthParameterIsConstReference(function, 0));
            }

            return false;
        });
    }

    /**
     * Checks if any elements of a set of overloaded operators takes a reference
     * to non <code>const</code>-qualified type as its right-hand side
     * parameter.
     *
     * @param names
     * An array of implicit names, like the one returned by
     * {@link ICPPASTUnaryExpression#getImplicitNames()}.
     * @return <code>true</code> iff. any of the overloaded operators in
     * <code>names</code> takes it right-hand side parameter via a
     * reference to non <code>const</code>-qualified type.
     */
    private static boolean hasAnyOperatorNonConstRhsParameter(IASTImplicitName... names) {
        return java.util.Arrays.stream(names).anyMatch(name -> {
            ICPPFunction function = Cast.as(ICPPFunction.class, name.getBinding());

            if (function == null) {
                return false;
            }

            return Function.nthParameterIsReference(function, function instanceof ICPPMethod ? 0 : 1) && !Function.nthParameterIsConstReference(
                    function, function instanceof ICPPMethod ? 0 : 1);
        });
    }

    /**
     * Check if <b>{@code name}</b> is dereferenced
     * <b>{@code nofDereferences}</b> times
     * <p>
     * This has the same conditional semantics as calling
     * {@code Expression.isDereferencedNTimes(name, nofDereferences, null)}
     *
     * @param name
     * The name to check
     * @param nofDereferences
     * The number of dereferences to check for
     * @return {@code true} iff. {@code name} is dereferenced
     * {@code nofDereferences} times, {@code false} otherwise
     */
    public static boolean isDereferencedNTimes(IASTName name, int nofDereferences) {
        return Expression.isDereferencedNTimes(name, nofDereferences, null);
    }

    /**
     * Check if <b>{@code name}</b> is dereferenced
     * <b>{@code nofDereferences}</b> times
     * <p>
     * Note that in multilevel pointers breakage can occur at every level.
     * Regardless, as long as the result of the breaking expression is a lower
     * dimensional pointer originating in <b>{@code name}</b>, you will get a
     * result of <b>{@code true}</b> for higher dimensions iff. the total chain
     * finishes at the desired dimension or another breakage occurs. Consider
     * the following example:
     * </p>
     *
     * <pre>
     * void f() {
     *   int * * * * ptr{};
     *   *(**((*ptr) += 2))++;
     * }
     * </pre>
     * <p>
     * Even though breakage occurs after the first dereference, you will get a
     * result of true for pointer level 3 and the maximum pointer level (here 4)
     * too because the result of the first breaking expression (here +=) is
     * still a lower dimensional (here 3) pointer originating in {@code ptr}. It
     * is trivial to see that the same holds for the result of the second
     * breaking expression (here ++) having a dimension of 1.
     * </p>
     * <p>
     * Note: The implementation currently interprets all overloaded operators as
     * chain-breakage.
     * </p>
     *
     * @param name
     * The name to check
     * @param nofDereferences
     * The number of dereferences to check for
     * @param breakingExpression
     * A reference wrapper used as an out parameter to store which
     * expression broke the dereference-chain.
     * @return <b>{@code true}</b> iff. <b>{@code name}</b> is dereferenced
     * <b>{@code nofDereferences}</b> times or non-fatal breakage (see
     * above) occurs at <b>{@code nofDereferences}</b>,
     * <b>{@code false}</b> otherwise. If <b>{@code false}</b> is
     * returned and <b>{@code breakingExpression}</b> is not
     * <b>{@code null}</b> it is set accordingly. Note that even if
     * <b>{@code false}</b> is returned,
     * <b>{@code breakingExpression}</b> might not be set if
     * <b>{@code name}</b> does not refer to a variable.
     */
    public static boolean isDereferencedNTimes(IASTName name, int nofDereferences, ReferenceWrapper<ICPPASTExpression> breakingExpression) {
        ICPPVariable variable;
        if ((variable = Cast.as(ICPPVariable.class, name.resolveBinding())) == null) {
            return false;
        }

        int seenDereferences = 0;

        IType type = SemanticUtil.getSimplifiedType(variable.getType());
        if (Type.isReference(type)) {
            ++seenDereferences;
            type = ((ICPPReferenceType) type).getType();
        }

        if (Type.isArray(type) || Type.isPointer(type)) {
            ICPPASTArraySubscriptExpression sub = Relation.getAncestorOf(ICPPASTArraySubscriptExpression.class, name);

            while (sub != null) {
                ++seenDereferences;
                sub = Relation.getAncestorOf(ICPPASTArraySubscriptExpression.class, sub.getParent());
            }
        }

        ICPPASTFieldReference fieldReference = Relation.getAncestorOf(ICPPASTFieldReference.class, name);
        while (fieldReference != null) {
            if (fieldReference.isPointerDereference()) {
                ++seenDereferences;
            }
            fieldReference = Relation.getAncestorOf(ICPPASTFieldReference.class, fieldReference.getParent());
        }

        ICPPASTExpression expression = Relation.getAncestorOf(ICPPASTExpression.class, name);
        while (expression != null) {
            if (expression instanceof ICPPASTUnaryExpression) {
                ICPPASTUnaryExpression unary = (ICPPASTUnaryExpression) expression;
                boolean isOverloadedOperator = unary.getImplicitNames().length != 0;
                boolean isModifyingOperation = Arrays.isAnyOf(unary.getOperator(), Expression.MODIFYING_UNARY_OPERATORS);
                boolean isAtDesiredLevel = seenDereferences == nofDereferences;
                if (isOverloadedOperator || (isAtDesiredLevel && isModifyingOperation)) {
                    if (breakingExpression != null) {
                        breakingExpression.set(expression);
                    }
                    break;
                } else if (unary.getOperator() == IASTUnaryExpression.op_star) {
                    ++seenDereferences;
                } else if (unary.getOperator() == IASTUnaryExpression.op_amper) {
                    --seenDereferences;
                }
            } else if (expression instanceof ICPPASTBinaryExpression) {
                ICPPASTBinaryExpression binary = (ICPPASTBinaryExpression) expression;
                boolean isOverloadedOperator = binary.getImplicitNames().length != 0;
                boolean isModifyingOperation = Arrays.isAnyOf(binary.getOperator(), Expression.MODIFYING_BINARY_OPERATORS);
                boolean isAtDesiredLevel = seenDereferences == nofDereferences;
                if (isOverloadedOperator || (isAtDesiredLevel && isModifyingOperation)) {
                    if (breakingExpression != null) {
                        breakingExpression.set(expression);
                    }
                    break;
                }
            } else if (expression instanceof ICPPASTFieldReference) {
                if (breakingExpression != null) {
                    breakingExpression.set(expression);
                }
                break;
            }

            expression = Relation.getAncestorOf(ICPPASTExpression.class, expression.getParent());
        }

        return seenDereferences == nofDereferences;
    }

    /**
     * Check if a given name is the left-hand operand of a given expression.
     *
     * @param entity
     * The entity to check against the expressions left-hand operand.
     * @param expression
     * The expression whose operand to check.
     * @return {@code true} iff the entity is the left hand operand of the binary expression
     */
    public static boolean isLeftHandOperandOf(ICPPASTName entity, ICPPASTBinaryExpression expression) {
        IBinding entityBinding = entity.resolveBinding();
        ICPPASTName operand = Expression.getResultingName((ICPPASTInitializerClause) expression.getOperand1());
        if (operand != null) {
            IBinding operandBinding = operand.resolveBinding();
            return entityBinding.equals(operandBinding);
        }

        return false;
    }

    /**
     * Check if a given binary expression possibly modifies the operand
     * specified via <code>side</code>
     *
     * @param expression
     * The binary expression to check
     * @param side
     * The side to check for modification
     * @return <code>true</code> iff. <code>expression</code> refers to an
     * operation to possibly modifies the operand specified by
     * <code>side</code>.
     * @see #isModifyingOperation(ICPPASTUnaryExpression)
     */
    public static boolean isModifyingOperation(ICPPASTBinaryExpression expression, OperatorSide side) {
        IASTImplicitName[] implicitNames = expression.getImplicitNames();

        if (side == OperatorSide.LeftHandSide) {
            if (implicitNames.length == 0) {
                return Arrays.isAnyOf(expression.getOperator(), Expression.MODIFYING_BINARY_OPERATORS);
            }

            return Expression.hasAnyOperatorNonConstLhsParameter(implicitNames);
        }

        return Expression.hasAnyOperatorNonConstRhsParameter(implicitNames);
    }

    /**
     * Check if a given unary expression possibly modifies its operand.
     *
     * An operation is considered to be possibly modifying if it either resolves
     * to one of the generic modifying unary operators or an overloaded operator
     * that might modify its argument.
     *
     * @param expression
     * The unary expression to check.
     * @return <code>true</code> iff. <code>expression</code> refers to a
     * possibly modifying operation.
     * @see #isModifyingOperation(ICPPASTBinaryExpression, OperatorSide)
     */
    public static boolean isModifyingOperation(ICPPASTUnaryExpression expression) {
        IASTImplicitName[] implicitNames = expression.getImplicitNames();

        if (implicitNames.length == 0) {
            return Arrays.isAnyOf(expression.getOperator(), Expression.MODIFYING_UNARY_OPERATORS);
        }

        return Expression.hasAnyOperatorNonConstLhsParameter(implicitNames);
    }

    public static boolean isResolvedToInstance(ICPPASTLiteralExpression thisLiteral) {
        return Expression.isResolvedToInstance(thisLiteral, null);
    }

    /**
     * Check if a given 'this' literal is resolved (e.g dereferenced) to an
     * instance of its class.
     *
     * @param thisLiteral
     * The literal to check. This must be a <b>{@code this}</b>
     * literal (e.g. a {@link ICPPASTLiteralExpression} with a kind
     * of {@link ICPPASTLiteralExpression#lk_this} or else all hell
     * might break loose.
     * @param breakingExpression
     * A reference wrapper to store which expression broke the access
     * chain
     * @return true, iff. the supplied literal is found in an expression that
     * dereferences it to an instance of its containing class-type.
     */
    public static boolean isResolvedToInstance(ICPPASTLiteralExpression thisLiteral, ReferenceWrapper<ICPPASTExpression> breakingExpression) {
        final int THIS_POINTER_DIMENSION = 1;
        int seenDereferences = 0;
        ICPPASTExpression outer = Cast.as(ICPPASTExpression.class, thisLiteral.getParent());

        while (outer != null) {
            boolean isAtInstanceLevel = seenDereferences == THIS_POINTER_DIMENSION;

            if (outer instanceof ICPPASTUnaryExpression) {
                ICPPASTUnaryExpression unary = (ICPPASTUnaryExpression) outer;
                boolean isOverloadedOperator = unary.getImplicitNames().length != 0;
                if (isAtInstanceLevel && isOverloadedOperator) {
                    if (breakingExpression != null) {
                        breakingExpression.set(unary);
                    }
                    return true;
                } else if (unary.getOperator() == IASTUnaryExpression.op_star) {
                    ++seenDereferences;
                } else if (unary.getOperator() == IASTUnaryExpression.op_amper) {
                    --seenDereferences;
                }
            } else if (outer instanceof ICPPASTBinaryExpression) {
                ICPPASTBinaryExpression binary = (ICPPASTBinaryExpression) outer;
                boolean isOverloadedOperator = binary.getImplicitNames().length != 0;
                if (isAtInstanceLevel && isOverloadedOperator) {
                    if (breakingExpression != null) {
                        breakingExpression.set(binary);
                    }
                    return true;
                }
            }

            outer = Cast.as(ICPPASTExpression.class, outer.getParent());
        }

        return seenDereferences == THIS_POINTER_DIMENSION;
    }

    /**
     * Check if a given name is the right-hand operand of a given expression.
     *
     * @param entity
     * The entity to check against the expressions right-hand
     * operand.
     * @param expression
     * The expression whose operand to check.
     * @return {@code true} iff the entity is the right hand operand of the binary expression
     */
    public static boolean isRightHandOperandOf(ICPPASTName entity, ICPPASTBinaryExpression expression) {
        IBinding entityBinding = entity.resolveBinding();
        ICPPASTName operand = Expression.getResultingName((ICPPASTInitializerClause) expression.getOperand2());
        if (operand != null) {
            IBinding operandBinding = operand.resolveBinding();
            return entityBinding.equals(operandBinding);
        }

        return false;
    }

    /**
     * Check if the given occurrence of a variable is used to access an element
     * of an array
     * <p>
     * This has the same operational semantics as calling
     * {@code Expression.isArrayElementAccess(occurrence, null)}
     * </p>
     *
     * @param occurrence
     * The occurrence of a variable to be checked
     * @return {@code true} iff. an array element is accessed via the given
     * occurrence, {@code false} otherwise.
     */
    public static boolean isArrayElementAccess(IASTName occurrence) {
        return isArrayElementAccess(occurrence, null);
    }

    /**
     * Check if the given occurrence of a variable is used to access an element
     * of an array
     *
     * @param occurrence
     * The occurrence of a variable to be checked
     * @param breakingExpression
     * A reference wrapper to store which expression broke the access
     * chain
     * @return {@code true} iff. an array element is accessed via the given
     * occurrence, {@code false} otherwise.
     */
    public static boolean isArrayElementAccess(IASTName occurrence, ReferenceWrapper<ICPPASTExpression> breakingExpression) {
        final ICPPVariable variable = Cast.as(ICPPVariable.class, occurrence.resolveBinding());
        return isDereferencedNTimes(occurrence, Type.arrayDimension(variable.getType()), breakingExpression);
    }

}
