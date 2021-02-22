package com.cevelop.ctylechecker.checker.includes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;
import com.cevelop.ctylechecker.infos.CtyleCheckerInfo;


public class MissingStandardIncludeChecker extends AbstractStandardIncludeChecker {

    private void addMissingStandardIncludeProblem(IASTName name) {
        reportProblem(ProblemId.MISSING_STD_INCLUDE, name, new CtyleCheckerInfo(nameToInclude.get(toQualifiedName(name))));
    }

    @Override
    public void processAst(IASTTranslationUnit ast) {
        reportMissingIncludes(ast);
        //specific check for istream/ostream/iosfwd
    }

    private void reportMissingIncludes(IASTTranslationUnit ast) {
        List<String> systemIncludes = Arrays.stream(ast.getIncludeDirectives()).filter(AbstractStandardIncludeChecker::isStandardInclude).map(
                MissingStandardIncludeChecker::extractIncludeName).collect(Collectors.toList());
        List<IASTName> allNames = namesOf(ast);
        allNames.stream().filter(AbstractStandardIncludeChecker::notPartOfQualifiedName).filter(AbstractStandardIncludeChecker::notPartOfTemplateId)
                .filter(AbstractStandardIncludeChecker::needsStandardInclude).filter(AbstractStandardIncludeChecker::notInMacroExpansion).filter((
                        name) -> !systemIncludes.contains(nameToInclude.get(toQualifiedName(name)))).forEach(this::addMissingStandardIncludeProblem);
    }
}
