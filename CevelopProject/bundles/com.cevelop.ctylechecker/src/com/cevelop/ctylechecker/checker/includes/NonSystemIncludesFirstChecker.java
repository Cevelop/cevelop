package com.cevelop.ctylechecker.checker.includes;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.ctylechecker.checker.AbstractStyleChecker;
import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;


public class NonSystemIncludesFirstChecker extends AbstractStyleChecker {

    private void reportOwnIncludeProblem(IASTPreprocessorIncludeStatement include) {
        reportProblem(ProblemId.NON_SYS_INCLUDES_FIRST, include);
    }

    private static boolean isSystemInclude(IASTPreprocessorIncludeStatement include) {
        return include.isSystemInclude();
    }

    private static boolean isNonSystemInclude(IASTPreprocessorIncludeStatement include) {
        return !isSystemInclude(include);
    }

    @Override
    public void processAst(IASTTranslationUnit ast) {
        IASTPreprocessorIncludeStatement[] includeDirectives = ast.getIncludeDirectives();
        Optional<IASTPreprocessorIncludeStatement> systemInclude = Arrays.stream(includeDirectives).filter(
                NonSystemIncludesFirstChecker::isSystemInclude).findFirst();
        if (systemInclude.isPresent()) {
            IASTPreprocessorIncludeStatement firstSystemInclude = systemInclude.get();
            int firstSystemIncludeOffset = firstSystemInclude.getFileLocation().getNodeOffset();
            Arrays.stream(includeDirectives).filter(include -> {
                int otherOffset = include.getFileLocation().getNodeOffset();
                return otherOffset > firstSystemIncludeOffset;
            }).filter(NonSystemIncludesFirstChecker::isNonSystemInclude).forEach(this::reportOwnIncludeProblem);
        }
    }
}
