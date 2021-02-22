package com.cevelop.codeanalysator.autosar.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IVariable;


public class AliasFinder {

    private final Set<IVariable>      knownAliases = new HashSet<>();
    private final IASTTranslationUnit translationUnit;

    public AliasFinder(IASTTranslationUnit translationUnit) {
        this.translationUnit = translationUnit;
    }

    public Optional<IVariable> findAlias(IASTExpression expression) {
        Optional<IVariable> alias = getAlias(expression);
        if (isKnownAlias(alias)) {
            return Optional.empty();
        } else {
            alias.ifPresent(knownAliases::add);
            return alias;
        }
    }

    private Optional<IVariable> getAlias(IASTExpression targetExpression) {
        IASTNode parent = targetExpression.getParent();
        if (parent instanceof IASTBinaryExpression) {
            IASTBinaryExpression binaryExpression = (IASTBinaryExpression) parent;
            return getAliasFromBinaryExpressionUsingTargetExpression(binaryExpression, targetExpression);
        } else if (parent instanceof IASTInitializer) {
            IASTInitializer initializer = (IASTInitializer) parent;
            return getAliasFromInitializerUsingTargetExpression(initializer);
        }
        return Optional.empty();
    }

    private Optional<IVariable> getAliasFromBinaryExpressionUsingTargetExpression(IASTBinaryExpression binaryExpression,
            IASTExpression targetExpression) {
        if (binaryExpression.getOperator() == IASTBinaryExpression.op_assign && binaryExpression.getOperand2() == targetExpression) {
            IASTNode leftOperandExpression = binaryExpression.getOperand1();
            if (leftOperandExpression instanceof IASTIdExpression) {
                IASTIdExpression aliasIdExpression = (IASTIdExpression) leftOperandExpression;
                IASTName aliasName = aliasIdExpression.getName();
                return getAliasFromName(aliasName);
            }
        }
        return Optional.empty();
    }

    private Optional<IVariable> getAliasFromInitializerUsingTargetExpression(IASTInitializer initializer) {
        IASTNode parent = initializer.getParent();
        if (parent instanceof IASTEqualsInitializer) {
            parent = parent.getParent();
        }
        if (parent instanceof IASTDeclarator) {
            IASTDeclarator aliasDeclaration = (IASTDeclarator) parent;
            IASTName aliasName = aliasDeclaration.getName();
            return getAliasFromName(aliasName);
        }
        return Optional.empty();
    }

    private Optional<IVariable> getAliasFromName(IASTName aliasName) {
        IBinding binding = aliasName.resolveBinding();
        if (binding instanceof IVariable) {
            return Optional.ofNullable((IVariable) binding);
        }
        return Optional.empty();
    }

    private boolean isKnownAlias(Optional<IVariable> alias) {
        return alias.map(knownAliases::contains).orElse(false);
    }

    public Collection<IASTIdExpression> getUsesOfAlias(IVariable alias) {
        return Arrays.stream(translationUnit.getReferences(alias)) //
                .map(IASTName::getParent) //
                .filter(IASTIdExpression.class::isInstance) //
                .map(IASTIdExpression.class::cast) //
                .collect(Collectors.toList());
    }
}
