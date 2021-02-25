package com.cevelop.codeanalysator.autosar.util;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;


public final class ScopeHelper {

    /**
     * Gets the scope for the binding.
     *
     * @param binding
     * the binding to get the scope for
     * @return the scope or <code>null</code> if there is an error
     */
    public static IScope getBindingScope(IBinding binding) {
        try {
            return binding.getScope();
        } catch (DOMException e) {
            return null;
        }
    }

    /**
     * Gets the scope of the lambda expression, which includes the lambda parameters.
     *
     * @param lambdaExpression
     * the lambda expression to get the scope for
     * @return the scope or <code>null</code> if there is an error
     */
    public static IScope getLambdaScope(ICPPASTLambdaExpression lambdaExpression) {
        IASTName lambdaName = lambdaExpression.getFunctionCallOperatorName();
        IBinding lambda = lambdaName.resolveBinding();
        try {
            return lambda.getScope();
        } catch (DOMException e) {
            return null;
        }
    }

    /**
     * Gets the parent scope.
     *
     * @param scope
     * the scope to get the parent for
     * @return the parent scope or <code>null</code> if there is an error
     */
    public static IScope getParentScope(IScope scope) {
        try {
            return scope.getParent();
        } catch (DOMException e) {
            return null;
        }
    }

    /**
     * Checks whether the scope is the same or a nested scope of the other scope.
     *
     * @param scope
     * the scope to check
     * @param of
     * the scope to check against
     * @return <code>true</code>, if <code>scope</code> is equal to or a nested scope of <code>of</code>,
     * <code>false</code> otherwise
     */
    public static boolean isSameOrChildScope(IScope scope, IScope of) {
        while (scope != of && scope != null) {
            scope = getParentScope(scope);
        }
        return scope == of;
    }

}
