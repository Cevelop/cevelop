package com.cevelop.ctylechecker.checker.classes;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.SemanticQueries;

import com.cevelop.ctylechecker.Infrastructure;
import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;


public class ExplicitConstructorChecker extends AbstractClassChecker {

    @Override
    public void processAst(IASTTranslationUnit ast) {
        List<ICPPASTFunctionDefinition> constructorDefinitions = constructorDefinitionsOf(ast);
        constructorDefinitions.forEach(this::reportIfNotExplicit);

        List<IASTDeclarator> constructorDeclarators = constructorDeclarationsOf(ast);
        constructorDeclarators.forEach(this::reportIfNotExplicit);
    }

    private static boolean isCallableWithSingleArgument(ICPPConstructor constructor) {
        ICPPParameter[] parameters = constructor.getParameters();
        if (parameters.length == 0) {
            return false;
        }
        int requiredArgumentCount = constructor.getRequiredArgumentCount();
        if (requiredArgumentCount > 1) {
            return false;
        }
        return !SemanticQueries.isCopyOrMoveConstructor(constructor) && !isinitializerListConstructor(constructor);
    }

    private static boolean isinitializerListConstructor(ICPPConstructor constructor) {
        ICPPParameter[] parameters = constructor.getParameters();
        if (parameters.length == 1) {
            ICPPParameter firstParameter = parameters[0];
            IType parameterType = firstParameter.getType();
            return ASTTypeUtil.getType(parameterType, true).startsWith("std::initializer_list<");
        }
        return false;
    }

    private void reportIfNotExplicit(ICPPASTFunctionDefinition constructor) {
        IASTFunctionDeclarator declarator = constructor.getDeclarator();
        if (needsExplicit(declarator)) {
            reportProblem(ProblemId.EXPLICIT_CONSTRUCTOR, constructor);
        }
    }

    private void reportIfNotExplicit(IASTDeclarator declarator) {
        if (needsExplicit(declarator)) {
            reportProblem(ProblemId.EXPLICIT_CONSTRUCTOR, declarator);
        }
    }

    private boolean needsExplicit(IASTDeclarator declarator) {
        IBinding functionBinding = declarator.getName().resolveBinding();
        ICPPConstructor constructorBinding = Infrastructure.as(functionBinding, ICPPConstructor.class);
        if (constructorBinding == null || constructorBinding.isExplicit()) {
            return false;
        }
        return isCallableWithSingleArgument(constructorBinding);
    }
}
