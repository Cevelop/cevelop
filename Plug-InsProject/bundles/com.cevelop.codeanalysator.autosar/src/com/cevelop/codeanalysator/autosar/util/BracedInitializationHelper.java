package com.cevelop.codeanalysator.autosar.util;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTImplicitNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;


public final class BracedInitializationHelper {

    /**
     * Checks whether parentheses are required to call the constructor used for initialization.
     * Parentheses are required when calling a constructor which would be ignored in favor of
     * a std::initializer_list constructor when using braced-initialization.
     *
     * @param initializer
     * the initializer to check
     * @return <code>true</code>, if parentheses are required to call the constructor,
     * <code>false</code> otherwise
     */
    public static boolean requiresParenthesisToCallConstructor(IASTInitializer initializer) {
        if (initializer instanceof ICPPASTConstructorInitializer) {
            ICPPASTConstructorInitializer paranthesesInitializer = (ICPPASTConstructorInitializer) initializer;
            IASTNode parent = paranthesesInitializer.getParent();
            if (parent instanceof IASTDeclarator) {
                IASTDeclarator parenthesesDeclarator = (IASTDeclarator) parent;
                Optional<ICPPConstructor> constructorCalledWithParentheses = getConstructorCalled(parenthesesDeclarator);
                if (constructorCalledWithParentheses.isPresent()) {
                    Optional<ICPPConstructor> constructorCalledWithBraces = getConstructorCalledWithBracedInitialization(parenthesesDeclarator);
                    return constructorCalledWithBraces.isPresent() && !constructorCalledWithParentheses.equals(constructorCalledWithBraces);
                }
            }
        }
        return false;
    }

    /**
     * Gets the constructor that is called for initialization of the declared variable
     * or <code>Optional.empty()</code> if there is none.
     *
     * @param declarator
     * the declarator of the variable to check for
     * @return the constructor called or <code>Optional.empty()</code>, if there is none
     */
    private static Optional<ICPPConstructor> getConstructorCalled(IASTDeclarator declarator) {
        if (declarator instanceof IASTImplicitNameOwner) {
            IASTImplicitNameOwner constructorNameOwner = (IASTImplicitNameOwner) declarator;
            IASTImplicitName[] implicitNames = constructorNameOwner.getImplicitNames();
            if (implicitNames.length == 1) {
                IBinding binding = implicitNames[0].resolveBinding();
                if (binding instanceof ICPPConstructor) {
                    ICPPConstructor constructor = (ICPPConstructor) binding;
                    return Optional.of(constructor);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the constructor that would be called using braced-initialization instead of parentheses
     * for initialization of the declared variable or <code>Optional.empty()</code> if there is none.
     *
     * @param paranthesesDeclarator
     * the declarator of the variable initialized using parentheses
     * @return the constructor called using braced-initialization
     * or <code>Optional.empty()</code>, if there is none
     */
    private static Optional<ICPPConstructor> getConstructorCalledWithBracedInitialization(IASTDeclarator paranthesesDeclarator) {
        ICPPNodeFactory factory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();
        IASTInitializer initializer = paranthesesDeclarator.getInitializer();
        if (initializer instanceof ICPPASTConstructorInitializer) {
            ICPPASTConstructorInitializer parenthesesInitializer = (ICPPASTConstructorInitializer) initializer;
            IASTInitializerList bracedInitializer = factory.newInitializerList();
            Arrays.stream(parenthesesInitializer.getArguments()) //
                    .map(IASTInitializerClause::copy) //
                    .forEach(bracedInitializer::addClause);
            IASTDeclarator bracedDeclarator = paranthesesDeclarator.copy();
            bracedDeclarator.setInitializer(bracedInitializer);
            bracedDeclarator.setParent(paranthesesDeclarator.getParent());
            return getConstructorCalled(bracedDeclarator);
        }
        return Optional.empty();
    }

}
