package com.cevelop.codeanalysator.autosar.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IEnumeration;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IValue;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCapture;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression.CaptureDefault;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;
import org.eclipse.cdt.internal.core.dom.parser.IntegralValue;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPEvaluation;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.TypeTraits;


public final class LambdaHelper {

    /**
     * Returns the return type of the lambda expression.
     *
     * @param lambdaExpression
     * the lambda expression
     * @return the return type
     */
    public static IType getLambdaReturnType(ICPPASTLambdaExpression lambdaExpression) {
        IBinding lambdaBinding = lambdaExpression.getFunctionCallOperatorName().resolveBinding();
        if (lambdaBinding instanceof IFunction) {
            IFunction function = (IFunction) lambdaBinding;

            IType type = function.getType();
            if (type instanceof IFunctionType) {
                IFunctionType functionType = (IFunctionType) type;
                return functionType.getReturnType();
            }
        }
        return null;
    }

    /**
     * Gets the enclosing lambda expression for this lambda expression or <code>null</code>, if there is none.
     * A Lambda expressions is only enclosed by lambda expressions in the same function.
     *
     * @param lambdaExpression
     * the lambda expression to get the enclosing lambda expression for
     * @return the enclosing lambda expression or <code>null</code>, if there is none
     */
    public static ICPPASTLambdaExpression getEnclosingLambda(ICPPASTLambdaExpression lambdaExpression) {
        for (IASTNode parent = lambdaExpression.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof IASTFunctionDefinition) {
                return null;
            }

            if (parent instanceof ICPPASTLambdaExpression) {
                ICPPASTLambdaExpression enclosingLambdaExpression = (ICPPASTLambdaExpression) parent;
                return enclosingLambdaExpression;
            }
        }
        return null;
    }

    /**
     * Searches for all variables captured implicitly by the lambda expression.
     * Contains <code>null</code>, if this is captured.
     *
     * @param lambdaExpression
     * the lambda expression to search for
     * @return the implicitly captured variables and <code>null</code>, if this is captured
     */
    public static Set<ICPPVariable> getImplicitlyCapturedVariables(ICPPASTLambdaExpression lambdaExpression) {
        Set<ICPPVariable> implicitlyCapturedVariables = new HashSet<>();
        if (lambdaExpression.getCaptureDefault() != CaptureDefault.UNSPECIFIED) {
            implicitlyCapturedVariables.addAll(getVariablesRequiringCaptures(lambdaExpression));
            implicitlyCapturedVariables.removeAll(getExplicitlyCapturedVariables(lambdaExpression));
        }
        return implicitlyCapturedVariables;
    }

    /**
     * Searches for all variables captured by the lambda expression, explicitly and implicitly.
     * Contains <code>null</code>, if this is captured.
     *
     * @param lambdaExpression
     * the lambda expression to search for
     * @return the explicitly or implicitly captured variables and <code>null</code>, if this is captured
     */
    private static Set<ICPPVariable> getAllCapturedVariables(ICPPASTLambdaExpression lambdaExpression) {
        Set<ICPPVariable> allCapturedVariables = getExplicitlyCapturedVariables(lambdaExpression);
        if (lambdaExpression.getCaptureDefault() != CaptureDefault.UNSPECIFIED) {
            allCapturedVariables.addAll(getVariablesRequiringCaptures(lambdaExpression));
        }
        return allCapturedVariables;
    }

    /**
     * Gets the variables captured explicitly by the lambda expression.
     * Contains <code>null</code>, if this is captured.
     *
     * @param lambdaExpression
     * the lambda expression to get the captures for
     * @return the explicitly captured variables and <code>null</code>, if this is captured
     */
    private static Set<ICPPVariable> getExplicitlyCapturedVariables(ICPPASTLambdaExpression lambdaExpression) {
        Set<ICPPVariable> explicitlyCapturedVariables = new HashSet<>();
        for (ICPPASTCapture capture : lambdaExpression.getCaptures()) {
            IASTName identifier = capture.getIdentifier();
            if (identifier == null) {
                explicitlyCapturedVariables.add(null);
            } else {
                IBinding binding = identifier.resolveBinding();
                if (binding instanceof ICPPVariable) {
                    ICPPVariable variable = (ICPPVariable) binding;
                    explicitlyCapturedVariables.add(variable);
                }
            }
        }
        return explicitlyCapturedVariables;
    }

    /**
     * Searches for all variables which are used by the lambda expression and require a capture.
     * Contains <code>null</code>, if this is captured.
     *
     * @param lambdaExpression
     * the lambda expression to search the variables for
     * @return the variables which require a capture and <code>null</code>, if this is captured
     */
    private static Set<ICPPVariable> getVariablesRequiringCaptures(ICPPASTLambdaExpression lambdaExpression) {
        Set<ICPPVariable> variablesRequiringCaptures = new HashSet<>();
        IScope lambdaScope = ScopeHelper.getLambdaScope(lambdaExpression);
        if (lambdaScope == null) {
            return variablesRequiringCaptures;
        }
        IASTNode body = lambdaExpression.getBody();
        body.accept(new ASTVisitor() {

            {
                shouldVisitExpressions = true;
            }

            @Override
            public int visit(IASTExpression expression) {
                if (expression instanceof IASTIdExpression) {
                    IASTIdExpression idExpression = (IASTIdExpression) expression;
                    IASTName name = idExpression.getName();
                    IBinding binding = name.resolveBinding();
                    if (binding instanceof ICPPVariable) {
                        ICPPVariable variable = (ICPPVariable) binding;
                        IScope variableScope = ScopeHelper.getBindingScope(variable);
                        if (ScopeHelper.isSameOrChildScope(lambdaScope, variableScope) && isPotentiallyEvaluated(idExpression)) {
                            if (!canUseWithoutCapturingVariable(variable)) {
                                variablesRequiringCaptures.add(variable);
                            } else if (!canUseWithoutCapturingThis(variable)) {
                                variablesRequiringCaptures.add(null);
                            }
                        }

                    }
                } else if (expression instanceof ICPPASTLambdaExpression) {
                    ICPPASTLambdaExpression innerLambdaExpression = (ICPPASTLambdaExpression) expression;
                    variablesRequiringCaptures.addAll(getAllCapturedVariables(innerLambdaExpression));
                    return PROCESS_SKIP;
                }
                return super.visit(expression);
            }
        });
        return variablesRequiringCaptures;
    }

    /**
     * Checks whether the expression is not an unevaluated operand or its subexpression.
     *
     * @param expression
     * the expression to check
     * @return <code>true</code>, if the expression is potentially evaluated,
     * <code>false</code> otherwise
     */
    private static boolean isPotentiallyEvaluated(IASTExpression expression) {
        IASTNode parent;
        for (parent = expression.getParent(); parent instanceof IASTExpression; parent = parent.getParent()) {
            if (parent instanceof IASTUnaryExpression) {
                IASTUnaryExpression unaryExpression = (IASTUnaryExpression) parent;
                switch (unaryExpression.getOperator()) {
                case IASTUnaryExpression.op_typeid:
                case IASTUnaryExpression.op_sizeof:
                case IASTUnaryExpression.op_noexcept:
                    return false;
                }
            }
        }
        if (parent instanceof IASTSimpleDeclSpecifier) {
            IASTSimpleDeclSpecifier simpleDeclSpecifier = (IASTSimpleDeclSpecifier) parent;
            if (simpleDeclSpecifier.getType() == IASTSimpleDeclSpecifier.t_decltype) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the variable can be used without capturing it by a lambda expression.
     *
     * @param variable
     * the variable to check
     * @return <code>true</code>, if the variable can be used without capturing it,
     * <code>false</code> otherwise
     */
    @SuppressWarnings("restriction")
    private static boolean canUseWithoutCapturingVariable(ICPPVariable variable) {
        if (isNonLocalOrStatic(variable)) {
            return true;
        }
        IType variableType = SemanticUtil.getNestedType(variable.getType(), SemanticUtil.TDEF);
        if (variableType instanceof ICPPReferenceType && isInitializedWithConstantExpression(variable)) {
            return true;
        }
        if (variableType instanceof IQualifierType) {
            IQualifierType qualifierType = (IQualifierType) variableType;
            if (qualifierType.isConst() && !qualifierType.isVolatile()) {
                IType qualifiedType = SemanticUtil.getNestedType(qualifierType.getType(), SemanticUtil.TDEF);
                if ((qualifiedType instanceof IBasicType || qualifiedType instanceof IEnumeration) && isInitializedWithConstantExpression(variable)) {
                    return true;
                }
            }
        }
        if (variable.isConstexpr() && TypeTraits.isTriviallyCopyable(variableType)) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether the variable can be used without capturing <code>this</code> by a lambda expression.
     *
     * @param variable
     * the variable to check
     * @return <code>true</code>, if the variable can be used without capturing <code>this</code>,
     * <code>false</code> otherwise
     */
    private static boolean canUseWithoutCapturingThis(ICPPVariable variable) {
        if (!(variable instanceof ICPPField)) {
            return true;
        } else {
            return variable.isStatic();
        }
    }

    /**
     * Checks whether a variable is not a local variable or has static storage duration.
     *
     * @param variable
     * the variable to check
     * @return <code>true</code>, if the variable is not local or static,
     * <code>false</code> otherwise
     */
    private static boolean isNonLocalOrStatic(ICPPVariable variable) {
        if (variable instanceof ICPPField) {
            return true;
        } else if (variable instanceof ICPPParameter) {
            return false;
        } else {
            IBinding variableOwner = variable.getOwner();
            if (variableOwner == null || variableOwner instanceof ICPPNamespace) {
                return true;
            } else if (variableOwner instanceof ICPPFunction || variableOwner instanceof ICPPClassType) {
                return variable.isStatic();
            }
        }
        return true;
    }

    /**
     * Checks whether the variable is initialized with a constant expression.
     *
     * @param variable
     * the variable to check
     * @return <code>true</code>, if the variable is initialized with a constant expression,
     * <code>false</code> otherwise
     */
    @SuppressWarnings("restriction")
    private static boolean isInitializedWithConstantExpression(ICPPVariable variable) {
        IValue initialValue = variable.getInitialValue();
        if (initialValue != null) {
            ICPPEvaluation evaluation = initialValue.getEvaluation();
            if (evaluation == null) {
                if (initialValue instanceof IntegralValue) {
                    return initialValue.numberValue() != null;
                }
            } else {
                return evaluation.isConstantExpression();
            }
        }
        return false;
    }

}
