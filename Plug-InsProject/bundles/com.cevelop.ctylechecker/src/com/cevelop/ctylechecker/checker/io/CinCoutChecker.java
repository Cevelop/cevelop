package com.cevelop.ctylechecker.checker.io;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.ctylechecker.Infrastructure;
import com.cevelop.ctylechecker.checker.AbstractStyleChecker;
import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;


public class CinCoutChecker extends AbstractStyleChecker {

    private static boolean isCinOrCout(IASTName nameNode) {
        String name = toQualifiedName(nameNode);
        return equalsAny(name, "std::cin", "std::cout");
    }

    private static boolean isOutsideMain(IASTName nameNode) {
        Class<IASTFunctionDefinition> type = IASTFunctionDefinition.class;
        IASTFunctionDefinition surroundingFunctionDefinition = Infrastructure.findAncestorWithType(nameNode, type);
        if (surroundingFunctionDefinition != null) {
            IASTFunctionDeclarator functionDeclarator = surroundingFunctionDefinition.getDeclarator();
            IASTName functionName = functionDeclarator.getName();
            return !functionName.toString().equals("main");
        }
        return true;
    }

    private void addCinCoutProblem(IASTName name) {
        reportProblem(ProblemId.CIN_COUT, name);
    }

    @Override
    public void processAst(IASTTranslationUnit ast) {
        List<IASTName> allNames = namesOf(ast);
        allNames.stream().filter(CinCoutChecker::isCinOrCout).filter(CinCoutChecker::isOutsideMain).forEach(this::addCinCoutProblem);
    }

}
