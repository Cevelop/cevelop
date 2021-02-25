package com.cevelop.constificator.core.util.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;

import com.cevelop.constificator.core.util.functional.UnaryPredicate;
import com.cevelop.constificator.core.util.type.Cast;


public class Function {

    /**
     * An enum to select how to match function types
     */
    public static enum MatchStyle {
        /**
         * Perform an exact type match
         */
        Exact,

        /**
         * Ignore const qualifications during type matching
         */
        IgnoreConstQualification,
    }

    private static final Map<MatchStyle, BiFunction<IType, IType, Boolean>> MATCHERS = new HashMap<>();

    static {
        MATCHERS.put(MatchStyle.Exact, (lhs, rhs) -> lhs.isSameType(rhs));
        MATCHERS.put(MatchStyle.IgnoreConstQualification, (lhs, rhs) -> Type.areSameTypeIgnoringConst(lhs, rhs));
    }

    /**
     * Check if the nth parameter of a function is a reference to
     * <code>const</code>.
     *
     * @param function
     * The function whose parameter to check.
     * @param n
     * The index of the parameter to check.
     * @return <code>true</code> iff. the <code>n</code>th parameter of
     * <code>suspect</code> is a reference to
     * <code>const</code>-qualified type.
     */
    public static boolean nthParameterIsConstReference(ICPPFunction function, int n) {
        ICPPParameter[] parameters = function.getParameters();
        if (parameters.length <= n) {
            return false;
        }

        ICPPParameter parameter = parameters[n];
        return Type.isReference(parameter.getType()) && Type.isConst(parameter.getType(), 1);
    }

    /**
     * Check if the nth parameter of a function is a reference to
     *
     * @param function
     * The function whose parameter to check.
     * @param n
     * The index of the parameter to check.
     * @return <code>true</code> iff. the <code>n</code>th parameter of
     * <code>suspect</code> is a reference.
     */
    public static boolean nthParameterIsReference(ICPPFunction function, int n) {
        ICPPParameter[] parameters = function.getParameters();
        if (parameters.length <= n) {
            return false;
        }

        ICPPParameter parameter = parameters[n];
        return Type.isReference(parameter.getType());
    }

    /**
     * Determine the argument indices of <code>name</code> in the argument list
     * <code>arguments</code>
     *
     * @param arguments
     * The argument list. Must no be null.
     * @param name
     * The C++ name for which to look up the indices. Must not be
     * null.
     * @param condition
     * A condition that the occurrences of <code>name</code> in the
     * argument list have to satisfy. Might be null.
     * @return A (possibly empty) list of indices
     */
    public static List<Integer> getArgumentIndicesFor(IASTInitializerClause[] arguments, ICPPASTName name, UnaryPredicate<IASTName> condition) {
        List<Integer> indices = new ArrayList<>();

        for (int index = 0; index < arguments.length; ++index) {
            ICPPASTInitializerClause clause = Cast.as(ICPPASTInitializerClause.class, arguments[index]);

            if (clause != null) {
                ICPPASTName argumentName = Expression.getResultingName(clause);

                if (argumentName != null && argumentName.resolveBinding().equals(name.resolveBinding())) {
                    if ((condition != null && condition.evaluate(argumentName)) || condition == null) {
                        indices.add(index);
                    }
                }
            }
        }

        return indices;
    }

    /**
     * Check if two functions have the same type with regard to the desired
     * {@link MatchStyle}
     *
     * @param lhs
     * The "left-hand-side" of the comparison
     * @param rhs
     * The "right-hand-side" of the comparison
     * @param style
     * How to match the types.
     * @return {@code true} iff. the function types match under the desired
     * matching style, {@code false} otherwise.
     */
    public static boolean haveSameType(ICPPFunction lhs, ICPPFunction rhs, MatchStyle style) {
        ICPPFunctionType lhsType = lhs.getType();
        ICPPFunctionType rhsType = rhs.getType();

        if (!lhsType.getReturnType().isSameType(rhsType.getReturnType())) {
            return false;
        }

        IType[] lhsParameterTypes = lhsType.getParameterTypes();
        IType[] rhsParameterTypes = rhsType.getParameterTypes();

        if (lhsParameterTypes.length != rhsParameterTypes.length) {
            return false;
        }

        BiFunction<IType, IType, Boolean> matcher = MATCHERS.get(style);
        for (int index = 0; index < lhsParameterTypes.length; ++index) {
            if (!matcher.apply(lhsParameterTypes[index], rhsParameterTypes[index])) {
                return false;
            }
        }

        return true;
    }

}
