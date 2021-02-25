package com.cevelop.constificator.core.util.semantic;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPDeferredFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionInstance;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;

import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.structural.Node;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;


public class Variable {

    /**
     * Check whether a given variable is used as a variadic argument in a
     * C-style varargs function
     *
     * @param name
     * The name of the variable
     * @param cache
     * The cached ASTRewrite {@link ASTRewriteCache}
     * @return true iff. is used in the variadic part of a varargs function.
     * false otherwise.
     */
    public static boolean isUsedAsVariadicInVarargsFunction(ICPPASTName name, ASTRewriteCache cache) {
        return Node.anyOfDescendingFrom(ICPPASTFunctionCallExpression.class, name, (ICPPASTFunctionCallExpression call, IASTName reference) -> {
            ICPPFunction function = Node.getBindingForFunction(call.getFunctionNameExpression());
            if (function == null || !function.takesVarArgs()) {
                return false;
            }

            IASTInitializerClause[] arguments = call.getArguments();
            int highestOccurrenceIndex = -1;
            for (int argumentIndex = 0; argumentIndex < arguments.length; ++argumentIndex) {
                ICPPASTName referencedName = Expression.getResultingName((ICPPASTInitializerClause) arguments[argumentIndex]);
                if (referencedName != null && referencedName.resolveBinding().equals(name.resolveBinding())) {
                    highestOccurrenceIndex = argumentIndex;
                }
            }

            if (highestOccurrenceIndex == -1) {
                return false;
            }

            return highestOccurrenceIndex >= function.getParameters().length;
        }, cache);
    }

    /**
     * Check whether a given variable is used in a call to a deferred function.
     *
     * Deferred functions arise when the function is a dependent name.
     *
     * @param name
     * The name of the variable
     * @param cache
     * The cached ASTRewrite {@link ASTRewriteCache}
     * @return true iff. the variable is used in a call to a deferred function.
     * false otherwise.
     */
    public static boolean isUsedInCallToDeferredFunction(ICPPASTName name, ASTRewriteCache cache) {
        IASTName[] references = Node.getReferences(name, cache);

        for (IASTName reference : references) {
            IASTFunctionCallExpression call;
            if ((call = Relation.getAncestorOf(IASTFunctionCallExpression.class, reference)) != null) {
                ICPPFunction function = Node.getBindingForFunction(call.getFunctionNameExpression());
                if (function instanceof ICPPDeferredFunction) {
                    return true;
                }

            }
        }

        return false;
    }

    /**
     * Check whether a given variable is used in a call to a template function.
     *
     * @param name
     * The name of the variable
     * @param cache
     * The cached ASTRewrite {@link ASTRewriteCache}
     * @return true iff. the variable is used in a call to a template function.
     * false otherwise.
     */
    public static boolean isUsedInCallToTemplateFunction(ICPPASTName name, ASTRewriteCache cache) {
        IASTName[] references = Node.getReferences(name, cache);

        for (IASTName reference : references) {
            IASTFunctionCallExpression call;
            if ((call = Relation.getAncestorOf(IASTFunctionCallExpression.class, reference)) != null) {
                ICPPFunction function = Node.getBindingForFunction(call.getFunctionNameExpression());
                if (function instanceof ICPPFunctionInstance) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Retrieve the {@link ICPPVariable} initialized by the initializer
     *
     * @param initializer
     * The initializer of the variable
     * @return The variable that is being initialized by the initializer if any,
     * {@code null} otherwise.
     */
    public static ICPPVariable getInitializedVariable(IASTInitializer initializer) {
        ICPPASTDeclarator declarator = Relation.getAncestorOf(ICPPASTDeclarator.class, initializer);
        ICPPASTConstructorChainInitializer memberInitializer = Relation.getAncestorOf(ICPPASTConstructorChainInitializer.class, initializer);

        if (declarator != null) {
            return Cast.as(ICPPVariable.class, declarator.getName().resolveBinding());
        } else if (memberInitializer != null) {
            return Cast.as(ICPPVariable.class, memberInitializer.getMemberInitializerId().resolveBinding());
        }

        return null;
    }
}
