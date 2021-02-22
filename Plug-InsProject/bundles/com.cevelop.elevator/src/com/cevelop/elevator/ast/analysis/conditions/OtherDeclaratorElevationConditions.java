package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCastExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTRangeBasedForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;

import com.cevelop.elevator.ast.analysis.NodeProperties;


/**
 * Asserts various small conditions in order to determine whether a
 * {@link IASTDeclarator} is eligible for elevation.
 */
public class OtherDeclaratorElevationConditions extends Condition {

    private IASTDeclarator declarator;
    private NodeProperties properties;

    @Override
    public boolean satifies(IASTNode node) {
        if (!(node instanceof IASTDeclarator)) {
            return false;
        }
        declarator = (IASTDeclarator) node;
        properties = new NodeProperties(node);
        return !isClassMember() && !isTemplateTypeSpecifier() && !isParameterDeclaration() && !hasAutoType() && !isPartOfCastExpression() &&
               !isPartOfTypedef() && !isInitializedAsRunVarInForLoop() && !isRangeDeclarationInRangeBasedFor() && !isTypeId() &&
               !isPartOfEqualsInitializationWithoutConstructorCall();
    }

    private boolean isTypeId() {
        return properties.hasAncestor(IASTTypeId.class) && !isNewExpression();
    }

    private boolean hasInitializer() {
        return declarator.getInitializer() != null;
    }

    private boolean isRangeDeclarationInRangeBasedFor() {
        return properties.hasAncestor(ICPPASTRangeBasedForStatement.class) && properties.getDistanceToAncestor(
                ICPPASTRangeBasedForStatement.class) == 2;
    }

    private boolean isInitializedAsRunVarInForLoop() {
        return hasInitializer() && properties.hasAncestor(ICPPASTForStatement.class) && properties.getDistanceToAncestor(
                ICPPASTForStatement.class) == 3;
    }

    private boolean isPartOfEqualsInitializationWithoutConstructorCall() {
        if (hasInitializerType(IASTEqualsInitializer.class)) {
            IASTInitializerClause clause = ((IASTEqualsInitializer) declarator.getInitializer()).getInitializerClause();
            if (clause instanceof ICPPASTFunctionCallExpression) {
                IASTImplicitName[] implicitNames = ((ICPPASTFunctionCallExpression) (clause)).getImplicitNames();
                return implicitNames.length == 0 || !(implicitNames[0].resolveBinding() instanceof ICPPConstructor);
            }
        }
        return false;
    }

    private boolean isPartOfTypedef() {
        return properties.hasAncestor(IASTSimpleDeclaration.class) && properties.getAncestor(IASTSimpleDeclaration.class).getDeclSpecifier()
                .getStorageClass() == IASTDeclSpecifier.sc_typedef;
    }

    private boolean isNewExpression() {
        return properties.hasAncestor(ICPPASTNewExpression.class);
    }

    private boolean isPartOfCastExpression() {
        return properties.hasAncestor(ICPPASTCastExpression.class);
    }

    private boolean hasInitializerType(Class<? extends IASTInitializer> initializerClass) {
        return declarator.getInitializer() != null && initializerClass.isInstance(declarator.getInitializer());
    }

    private boolean isParameterDeclaration() {
        return declarator.getParent() instanceof ICPPASTParameterDeclaration;
    }

    private boolean isTemplateTypeSpecifier() {
        return properties.hasAncestor(ICPPASTTemplateId.class);
    }

    private boolean hasAutoType() {
        NodeProperties properties = new NodeProperties(declarator);
        if (!properties.hasAncestor(IASTSimpleDeclaration.class)) {
            return false;
        }
        IASTSimpleDeclaration declaration = properties.getAncestor(IASTSimpleDeclaration.class);
        IASTDeclSpecifier declSpecifier = declaration.getDeclSpecifier();
        return (declSpecifier instanceof IASTSimpleDeclSpecifier && ((IASTSimpleDeclSpecifier) declSpecifier)
                .getType() == IASTSimpleDeclSpecifier.t_auto);
    }

    private boolean isClassMember() {
        // Parent is the IASTSimpleDeclaration, grandparent is potentially a class
        return (declarator.getParent().getParent() instanceof ICPPASTCompositeTypeSpecifier);
    }
}
