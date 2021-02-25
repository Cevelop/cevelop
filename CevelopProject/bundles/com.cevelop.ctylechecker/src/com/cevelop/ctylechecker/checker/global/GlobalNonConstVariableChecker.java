package com.cevelop.ctylechecker.checker.global;

import static com.cevelop.ctylechecker.Infrastructure.as;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;

import com.cevelop.ctylechecker.checker.AbstractStyleChecker;
import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;


public class GlobalNonConstVariableChecker extends AbstractStyleChecker {

    private static boolean isNonConstPointer(IASTPointerOperator operator) {
        IASTPointer pointer = as(operator, IASTPointer.class);
        if (pointer != null) {
            return !pointer.isConst();
        }
        return false;
    }

    private static boolean isNonConstVariableDeclarator(IASTDeclarator declarator) {
        IASTPointerOperator[] pointerOperators = declarator.getPointerOperators();
        return !(declarator instanceof IASTFunctionDeclarator) && Arrays.stream(pointerOperators).anyMatch(
                GlobalNonConstVariableChecker::isNonConstPointer);
    }

    private static boolean isVariableDeclarator(IASTDeclarator declarator) {
        return !(declarator instanceof IASTFunctionDeclarator);
    }

    private static boolean containsNonConstVariableDeclaration(IASTDeclaration declaration) {
        IASTSimpleDeclaration simpleDeclaration = as(declaration, IASTSimpleDeclaration.class);
        if (simpleDeclaration != null) {
            IASTDeclSpecifier declSpecifier = simpleDeclaration.getDeclSpecifier();
            if (isTypedef(declSpecifier)) {
                return false;
            }
            if (isConstOrConstexpr(declSpecifier)) {
                return Arrays.stream(simpleDeclaration.getDeclarators()).anyMatch(GlobalNonConstVariableChecker::isNonConstVariableDeclarator);
            } else {
                return Arrays.stream(simpleDeclaration.getDeclarators()).anyMatch(GlobalNonConstVariableChecker::isVariableDeclarator);
            }
        }
        return false;
    }

    private static boolean isTypedef(IASTDeclSpecifier declSpecifier) {
        return declSpecifier.getStorageClass() == IASTDeclSpecifier.sc_typedef;
    }

    private static boolean isConstOrConstexpr(IASTDeclSpecifier declSpecifier) {
        ICPPASTDeclSpecifier cppDeclSpecifier = as(declSpecifier, ICPPASTDeclSpecifier.class);
        if (cppDeclSpecifier != null && cppDeclSpecifier.isConstexpr()) {
            return true;
        }
        return declSpecifier.isConst();
    }

    private void reportNonConstVariable(IASTDeclaration declaration) {
        reportProblem(ProblemId.GLOBAL_NON_CONST_VAR, declaration);
    }

    private static List<ICPPASTNamespaceDefinition> getNamespaces(IASTNode node) {
        List<ICPPASTNamespaceDefinition> namespaces = new ArrayList<>();
        node.accept(new ASTVisitor() {

            {
                shouldVisitNamespaces = true;
            }

            @Override
            public int visit(ICPPASTNamespaceDefinition namespaceDefinition) {
                namespaces.add(namespaceDefinition);
                return super.visit(namespaceDefinition);
            }
        });
        return namespaces;
    }

    @Override
    public void processAst(IASTTranslationUnit ast) {
        IASTDeclaration[] declarations = ast.getDeclarations();
        reportNonConstVariables(declarations);
        List<ICPPASTNamespaceDefinition> namespaces = getNamespaces(ast);
        namespaces.stream().map(ns -> ns.getDeclarations()).forEach(this::reportNonConstVariables);
    }

    private void reportNonConstVariables(IASTDeclaration[] declarations) {
        Arrays.stream(declarations).filter(GlobalNonConstVariableChecker::containsNonConstVariableDeclaration).forEach(this::reportNonConstVariable);
    }

}
