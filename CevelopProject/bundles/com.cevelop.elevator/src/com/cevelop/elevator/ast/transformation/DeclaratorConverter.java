package com.cevelop.elevator.ast.transformation;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;


/**
 * Converts declarators into C++11 initializer lists.
 *
 */

public class DeclaratorConverter {

    private final IASTDeclarator declarator;
    private IASTDeclarator       result;

    public DeclaratorConverter(IASTDeclarator declarator) {
        this.declarator = declarator;
    }

    public IASTDeclarator convert() {
        final IASTInitializer initializer = declarator.getInitializer();
        if (initializer instanceof IASTEqualsInitializer) {
            IASTEqualsInitializer eqInitializer = (IASTEqualsInitializer) initializer;
            if (eqInitializer.getInitializerClause() instanceof ICPPASTFunctionCallExpression) {
                transformConstructorCall(eqInitializer);
            } else if (eqInitializer.getInitializerClause() instanceof ICPPASTSimpleTypeConstructorExpression) {
                transformCopyConstructorWithUniformInitializedInitializer(eqInitializer);
            } else {
                transformCopyConstruction(eqInitializer);
            }
        } else if (initializer instanceof ICPPASTConstructorInitializer) {
            transformConstructorInitializer(initializer);
        } else {
            transformUninitializedDeclaration();
        }
        return result;
    }

    /**
     * T t gets transformed to T t { }
     */
    private void transformUninitializedDeclaration() {
        result = createDeclarator(createInitializerList());
    }

    /**
     * T t(x) gets transformed to T t { x }
     *
     * @param initializer
     */
    private void transformConstructorInitializer(final IASTInitializer initializer) {
        IASTInitializerList initializerList = createInitializerList(((ICPPASTConstructorInitializer) initializer).getArguments());
        result = createDeclarator(initializerList);
    }

    /**
     * T t = x gets transformed to T t { x }
     *
     * @param eqInitializer
     */
    private void transformCopyConstruction(IASTEqualsInitializer eqInitializer) {
        IASTInitializerList initializerList = createInitializerList();
        initializerList.addClause(eqInitializer.getInitializerClause().copy());
        result = createDeclarator(initializerList);
    }

    /**
     * T t = T { } gets transformed to T t { }
     *
     * @param eqInitializer
     */
    private void transformCopyConstructorWithUniformInitializedInitializer(IASTEqualsInitializer eqInitializer) {
        IASTInitializer initializerList = ((ICPPASTSimpleTypeConstructorExpression) eqInitializer.getInitializerClause()).getInitializer();
        if (initializerList instanceof IASTInitializerList) {
            result = createDeclarator(initializerList.copy(CopyStyle.withoutLocations));
        }
    }

    /**
     * T t = T() gets transformed to T t { }
     *
     * @param eqInitializer
     */
    private void transformConstructorCall(IASTEqualsInitializer eqInitializer) {
        ICPPASTFunctionCallExpression constructorCall = (ICPPASTFunctionCallExpression) eqInitializer.getInitializerClause();
        IASTInitializerList initializerList = createInitializerList(constructorCall.getArguments());
        result = createDeclarator(initializerList);
    }

    private IASTInitializerList createInitializerList(IASTInitializerClause[] arguments) {
        IASTInitializerList initializerList = createInitializerList();
        for (IASTInitializerClause clause : arguments) {
            initializerList.addClause(clause.copy());
        }
        return initializerList;
    }

    private IASTDeclarator createDeclarator(IASTInitializer initializer) {
        IASTDeclarator convertedDeclarator = declarator.copy();
        convertedDeclarator.setInitializer(initializer);
        return convertedDeclarator;
    }

    private IASTInitializerList createInitializerList() {
        return declarator.getTranslationUnit().getASTNodeFactory().newInitializerList();
    }
}
