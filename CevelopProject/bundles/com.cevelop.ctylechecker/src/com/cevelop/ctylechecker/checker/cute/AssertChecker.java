package com.cevelop.ctylechecker.checker.cute;

import static com.cevelop.ctylechecker.Infrastructure.as;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;

import com.cevelop.ctylechecker.Infrastructure;
import com.cevelop.ctylechecker.checker.AbstractStyleChecker;
import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;


public class AssertChecker extends AbstractStyleChecker {

    private static final String CUTE_HEADER = "cute.h";

    private static boolean isNullaryVoidFunction(IASTFunctionDefinition function) {
        return isNullaryFunction(function) && isVoidFunction(function);
    }

    private static boolean isVoidFunction(IASTFunctionDefinition function) {
        IASTSimpleDeclSpecifier declSpec = as(function.getDeclSpecifier(), IASTSimpleDeclSpecifier.class);
        if (declSpec != null) {
            return declSpec.getType() == IASTSimpleDeclSpecifier.t_void;
        }
        return false;
    }

    private static boolean isNullaryFunction(IASTFunctionDefinition function) {
        ICPPASTFunctionDeclarator declarator = as(function.getDeclarator(), ICPPASTFunctionDeclarator.class);
        if (declarator != null) {
            ICPPASTParameterDeclaration[] parameters = declarator.getParameters();
            return parameters == null || parameters.length == 0;
        }
        return false;
    }

    private static boolean isAssertMacro(IASTNodeLocation nodeLocation) {
        IASTMacroExpansionLocation expansion = as(nodeLocation, IASTMacroExpansionLocation.class);
        // FIXME! does not report
        if (expansion != null) {
            return expansion.getExpansion().getMacroDefinition().getName().toString().startsWith("ASSERT");
        }
        return false;
    }

    private void reportAssert(IASTNodeLocation location) {
        IProblemLocation problemLocation = Infrastructure.createProblemLocation(location, getFile());
        reportProblem(ProblemId.MULTIPLE_ASSERTS, problemLocation);
    }

    private void reportMultipleAsserts(IASTFunctionDefinition function) {
        Arrays.stream(function.getNodeLocations()).filter(AssertChecker::isAssertMacro).skip(1).forEach(this::reportAssert);
    }

    @Override
    public void processAst(IASTTranslationUnit ast) {
        if (hasIncludeToCuteH(ast)) {
            List<IASTFunctionDefinition> functionDefinitions = getFunctionDefintions(ast);
            functionDefinitions.stream().filter(AssertChecker::isNullaryVoidFunction).forEach(this::reportMultipleAsserts);
        }
    }

    private List<IASTFunctionDefinition> getFunctionDefintions(IASTTranslationUnit ast) {
        List<IASTFunctionDefinition> functionDefinitions = new ArrayList<>();
        ast.accept(new ASTVisitor() {

            {
                shouldVisitDeclarations = true;
            }

            @Override
            public int visit(IASTDeclaration declaration) {
                if (declaration instanceof IASTFunctionDefinition) {
                    functionDefinitions.add((IASTFunctionDefinition) declaration);
                }
                return super.visit(declaration);
            }
        });
        return functionDefinitions;
    }

    private static boolean isIncludeToCuteH(IASTPreprocessorIncludeStatement include) {
        return !include.isSystemInclude() && include.getName().toString().equals(CUTE_HEADER);
    }

    private boolean hasIncludeToCuteH(IASTTranslationUnit ast) {
        IASTPreprocessorIncludeStatement[] includeDirectives = ast.getIncludeDirectives();
        return Arrays.stream(includeDirectives).anyMatch(AssertChecker::isIncludeToCuteH);
    }

}
