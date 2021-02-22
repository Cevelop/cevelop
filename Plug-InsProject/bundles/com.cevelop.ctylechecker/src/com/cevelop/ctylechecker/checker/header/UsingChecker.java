package com.cevelop.ctylechecker.checker.header;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;

import com.cevelop.ctylechecker.Infrastructure;
import com.cevelop.ctylechecker.checker.AbstractStyleChecker;
import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;


public class UsingChecker extends AbstractStyleChecker {

    private static boolean isInGlobalNamespace(IASTDeclaration declaration) {
        return Infrastructure.findAncestorWithType(declaration.getParent(), IASTDeclaration.class) == null;
    }

    private void addUsingProblem(IASTNode using) {
        reportProblem(ProblemId.USING_DIRECTIVE, using);
    }

    @Override
    public void processAst(IASTTranslationUnit ast) {
        if (ast.isHeaderUnit()) {
            List<ICPPASTUsingDirective> usingDirectives = usingDirectivesOf(ast);
            usingDirectives.stream().filter(UsingChecker::isInGlobalNamespace).forEach(this::addUsingProblem);
            List<ICPPASTUsingDeclaration> usingDeclarations = usingDeclarationsOf(ast);
            usingDeclarations.stream().filter(UsingChecker::isInGlobalNamespace).forEach(this::addUsingProblem);
        }
    }
}
