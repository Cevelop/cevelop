package com.cevelop.codeanalysator.autosar.util;

import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;


public final class AutoHelper {

    /**
     * Checks whether the variables are declared with the <code>auto</code> or <code>decltype(auto)</code> type specifiers.
     *
     * @param simpleDeclaration
     * the variables declaration to check
     * @return <code>true</code>, if the declaration is using the <code>auto</code> type specifier,
     * <code>false</code> otherwise
     */
    public static boolean isAutoDeclaringVariables(IASTSimpleDeclaration simpleDeclaration) {
        IASTDeclSpecifier declSpec = simpleDeclaration.getDeclSpecifier();
        return isAutoDeclSpecifier(declSpec);
    }

    /**
     * Checks whether all variables are initialized with a function call or a initializer of a non-fundamental type.
     *
     * @param simpleDeclaration
     * the variables declaration to check
     * @return <code>true</code>, if all variables are initialized with a function call or constructor of non-fundamental type,
     * <code>false</code> otherwise
     */
    public static boolean isInitializingVariablesWithFunctionCallOrInitializerOfNonFundamentalType(IASTSimpleDeclaration simpleDeclaration) {
        return Arrays.stream(simpleDeclaration.getDeclarators()).allMatch(
                AutoHelper::isInitializingWithFunctionCallOrInitializerOfNonFundamentalType);
    }

    /**
     * Checks whether the function is declared with the <code>auto</code> keyword for trailing return type syntax
     * or with the <code>auto</code> or <code>decltype(auto)</code> type specifiers for return type deduction.
     *
     * @param functionDefinition
     * the function declaration to check
     * @return <code>true</code>, if the declaration is using the <code>auto</code> keyword
     * or <code>auto</code> or <code>decltype(auto)</code> type specifiers,
     * <code>false</code> otherwise
     */
    public static boolean isAutoDeclaringFunction(IASTFunctionDefinition functionDefinition) {
        IASTDeclSpecifier declSpec = functionDefinition.getDeclSpecifier();
        return isAutoDeclSpecifier(declSpec);
    }

    /**
     * Checks whether the declaration is declaring a template function using trailing return type syntax.
     *
     * @param functionDefinition
     * the function declaration to check
     * @return <code>true</code>, if it is declaring a template function using trailing return type syntax,
     * <code>false</code> otherwise
     */
    public static boolean isDeclaringTemplateFunctionWithTrailingReturnTypeSyntax(IASTFunctionDefinition functionDefinition) {
        if (functionDefinition.getParent() instanceof ICPPASTTemplateDeclaration) {
            IASTFunctionDeclarator functionDeclarator = functionDefinition.getDeclarator();
            if (functionDeclarator instanceof ICPPASTFunctionDeclarator) {
                ICPPASTFunctionDeclarator cppFunctionDeclarator = (ICPPASTFunctionDeclarator) functionDeclarator;
                return cppFunctionDeclarator.getTrailingReturnType() != null;
            }
        }
        return false;
    }

    /**
     * Checks whether the <code>auto</code> or <code>decltype(auto)</code> specifiers are used.
     *
     * @param declSpec
     * the specifier to check
     * @return <code>true</code>, if the declaration is using the <code>auto</code> or <code>decltype(auto)</code> specifiers,
     * <code>false</code> otherwise
     */
    private static boolean isAutoDeclSpecifier(IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTSimpleDeclSpecifier) {
            ICPPASTSimpleDeclSpecifier simpleDeclSpecifier = (ICPPASTSimpleDeclSpecifier) declSpec;
            int type = simpleDeclSpecifier.getType();
            return type == IASTSimpleDeclSpecifier.t_auto || type == IASTSimpleDeclSpecifier.t_decltype_auto;
        }
        return false;
    }

    /**
     * Checks whether the declarator initializes the variable with a function call or a initializer of a non-fundamental type.
     *
     * @param declarator
     * the declarator to check
     * @return <code>true</code>, if the variable is initialized with a function call or initializer of non-fundamental type,
     * <code>false</code> otherwise
     */
    private static boolean isInitializingWithFunctionCallOrInitializerOfNonFundamentalType(IASTDeclarator declarator) {
        IASTInitializerClause initializerClause = getAutoInitClauseForDeclarator(declarator);
        if (initializerClause instanceof IASTFunctionCallExpression) {
            return true;
        } else if (initializerClause instanceof ICPPASTSimpleTypeConstructorExpression) {
            ICPPASTSimpleTypeConstructorExpression constructorExpression = (ICPPASTSimpleTypeConstructorExpression) initializerClause;
            return !(constructorExpression.getExpressionType() instanceof IBasicType);
        } else if (initializerClause instanceof ICPPASTLambdaExpression) {
            return true;
        }
        return false;
    }

    /**
     * Gets the initializer clause for a declarator in a <code>auto</code> or <code>decltype(auto)</code> declaration.
     *
     * @param declarator
     * the declarator to get the initializer clause for
     * @return the initializer clause or <code>null</code>,
     * if not a valid <code>auto</code> or <code>decltype(auto)</code> initializer
     */
    private static IASTInitializerClause getAutoInitClauseForDeclarator(IASTDeclarator declarator) {
        IASTInitializer initializer = declarator.getInitializer();
        if (initializer instanceof IASTEqualsInitializer) {
            IASTEqualsInitializer equalsInitializer = (IASTEqualsInitializer) initializer;
            return equalsInitializer.getInitializerClause();
        } else if (initializer instanceof ICPPASTConstructorInitializer) {
            ICPPASTConstructorInitializer constructorInitializer = (ICPPASTConstructorInitializer) initializer;
            IASTInitializerClause[] initializerClauses = constructorInitializer.getArguments();
            if (initializerClauses.length == 1) {
                return initializerClauses[0];
            }
        } else if (initializer instanceof IASTInitializerList) {
            IASTInitializerList initializerList = (IASTInitializerList) initializer;
            IASTInitializerClause[] initializerClauses = initializerList.getClauses();
            if (initializerClauses.length == 1) {
                return initializerClauses[0];
            }
        }
        return null;
    }

}
