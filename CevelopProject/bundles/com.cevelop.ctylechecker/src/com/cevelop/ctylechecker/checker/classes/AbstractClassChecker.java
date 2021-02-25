package com.cevelop.ctylechecker.checker.classes;

import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;

import com.cevelop.ctylechecker.Infrastructure;
import com.cevelop.ctylechecker.checker.AbstractStyleChecker;


public abstract class AbstractClassChecker extends AbstractStyleChecker {

    protected static List<IASTDeclarator> constructorDeclarationsOf(IASTNode node) {
        final List<IASTDeclarator> allConstructorDeclarators = new ArrayList<>();
        node.accept(new ASTVisitor() {

            {
                shouldVisitDeclarations = true;
            }

            @Override
            public int visit(IASTDeclaration declaration) {
                if (declaration instanceof IASTSimpleDeclaration) {
                    IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
                    List<IASTDeclarator> constructorDeclarators = getConstructorDeclarators(simpleDeclaration);
                    allConstructorDeclarators.addAll(constructorDeclarators);
                }
                return PROCESS_CONTINUE;
            }
        });
        return allConstructorDeclarators;
    }

    protected static List<ICPPASTFunctionDefinition> constructorDefinitionsOf(IASTNode node) {
        final List<ICPPASTFunctionDefinition> allConstructorDefinitions = new ArrayList<>();
        node.accept(new ASTVisitor() {

            {
                shouldVisitDeclarations = true;
            }

            @Override
            public int visit(IASTDeclaration declaration) {
                if (declaration instanceof ICPPASTFunctionDefinition) {
                    ICPPASTFunctionDefinition functionDefinition = (ICPPASTFunctionDefinition) declaration;
                    Optional<ICPPConstructor> constructorBinding = getConstructorBinding(functionDefinition);
                    constructorBinding.ifPresent(ignored -> {
                        allConstructorDefinitions.add(functionDefinition);
                    });
                }
                return PROCESS_CONTINUE;
            }
        });
        return allConstructorDefinitions;
    }

    protected static Optional<ICPPConstructor> getConstructorBinding(ICPPASTFunctionDefinition functionDefinition) {
        IASTName functionName = functionDefinition.getDeclarator().getName();
        IBinding functionBinding = functionName.resolveBinding();
        return Infrastructure.asOpt(functionBinding, ICPPConstructor.class);
    }

    protected static List<IASTDeclarator> getConstructorDeclarators(IASTSimpleDeclaration simpleDeclaration) {
        return stream(simpleDeclaration.getDeclarators()).filter(declarator -> declarator.getName().resolveBinding() instanceof ICPPConstructor)
                .collect(Collectors.toList());
    }
}
