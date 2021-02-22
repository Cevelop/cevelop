package com.cevelop.ctylechecker.checker.io;

import static com.cevelop.ctylechecker.Infrastructure.as;

import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.ctylechecker.checker.AbstractStyleChecker;
import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;


public class IostreamChecker extends AbstractStyleChecker {

    private static final String MAIN_FUNCTION_NAME = "main";
    private static final String IOSTREAM_NAME      = "iostream";

    private static boolean isIncludeToIostream(IASTPreprocessorIncludeStatement include) {
        return include.isSystemInclude() && include.getName().toString().equals(IOSTREAM_NAME);
    }

    private void addIostreamProblem(IASTPreprocessorIncludeStatement include) {
        reportProblem(ProblemId.IOSTREAM, include);
    }

    @Override
    public void processAst(IASTTranslationUnit ast) {
        if (!containsMainFunction(ast)) {
            IASTPreprocessorIncludeStatement[] includeDirectives = ast.getIncludeDirectives();
            Arrays.stream(includeDirectives).filter(IostreamChecker::isIncludeToIostream).forEach(this::addIostreamProblem);
        }
    }

    private boolean containsMainFunction(IASTTranslationUnit ast) {
        return Arrays.stream(ast.getDeclarations()).anyMatch(IostreamChecker::isMainFunctionDefinition);
    }

    private static boolean isMainFunctionDefinition(IASTDeclaration declaration) {
        IASTFunctionDefinition functionDefinition = as(declaration, IASTFunctionDefinition.class);
        if (functionDefinition != null) {
            IASTFunctionDeclarator functionDeclarator = functionDefinition.getDeclarator();
            IASTName functionName = functionDeclarator.getName();
            return functionName.toString().equals(MAIN_FUNCTION_NAME);
        }
        return false;
    }
}
