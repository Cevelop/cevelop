package com.cevelop.ctylechecker.checker.includes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;
import com.cevelop.ctylechecker.infos.CtyleCheckerInfo;


public class SuperfluousStandardIncludeChecker extends AbstractStandardIncludeChecker {

    private void addSuperfluousIncludeProblem(IASTPreprocessorIncludeStatement include) {
        reportProblem(ProblemId.SUPERFLUOUS_STD_INCLUDE, include, new CtyleCheckerInfo(extractIncludeName(include)));
    }

    @Override
    public void processAst(IASTTranslationUnit ast) {
        reportSuperfluousIncludes(ast);
        //specific check for istream/ostream/iosfwd
    }

    private void reportSuperfluousIncludes(IASTTranslationUnit ast) {
        List<IASTPreprocessorIncludeStatement> systemIncludes = Arrays.stream(ast.getIncludeDirectives()).filter(
                AbstractStandardIncludeChecker::isStandardInclude).collect(Collectors.toList());
        List<IASTName> allNames = namesOf(ast);
        systemIncludes.stream().filter(include -> !isRequired(include, allNames)).forEach(this::addSuperfluousIncludeProblem);
    }
}
