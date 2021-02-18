package com.cevelop.ctylechecker.checker.header;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorEndifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorPragmaStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.ctylechecker.checker.AbstractStyleChecker;
import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;
import com.cevelop.ctylechecker.infos.CtyleCheckerInfo;


public class IncludeGuardChecker extends AbstractStyleChecker {

    @Override
    public void processAst(IASTTranslationUnit ast) {
        if (ast.isHeaderUnit()) {
            String fileName = getFile().getName().toUpperCase().replaceAll("[\\.-]", "_") + '_';
            IASTPreprocessorStatement[] preprocessorStatements = ast.getAllPreprocessorStatements();

            if (preprocessorStatements.length < 1) {
                reportIncludeGuardMissing(ast, fileName);
                return;
            }

            IASTPreprocessorStatement firstPPStatement = preprocessorStatements[0];

            if (isPragmaOnce(firstPPStatement)) {
                return;
            } else if (preprocessorStatements.length < 3)
            {
                reportIncludeGuardMissing(ast, fileName);
                return;
            }

            IASTPreprocessorStatement secondPPStatement = preprocessorStatements[1];
            IASTPreprocessorStatement lastPPStatement = preprocessorStatements[preprocessorStatements.length - 1];

            if (!hasExpectedForm(firstPPStatement, secondPPStatement, lastPPStatement)) {
                reportIncludeGuardMissing(ast, fileName);
            }
        }
    }

    private boolean isPragmaOnce(IASTPreprocessorStatement statement) {
        if(statement instanceof IASTPreprocessorPragmaStatement) {
            IASTPreprocessorPragmaStatement pragma = (IASTPreprocessorPragmaStatement) statement;
            return String.valueOf(pragma.getMessage()).equals("once");
        }
        return false;
    }

    private boolean hasExpectedForm(IASTPreprocessorStatement first, IASTPreprocessorStatement second, IASTPreprocessorStatement last) {
        return checkSecond(second, checkFirst(first)) && checkLast(last);
    }

    private boolean checkLast(IASTPreprocessorStatement last) {
        return last instanceof IASTPreprocessorEndifStatement;
    }

    private boolean checkSecond(IASTPreprocessorStatement second, String condition) {
        if (second instanceof IASTPreprocessorMacroDefinition) {
            IASTPreprocessorMacroDefinition macroDefinition = (IASTPreprocessorMacroDefinition) second;
            String expansion = macroDefinition.getName().toString();
            if (expansion.equals(condition)) {
                return true;
            }
        }
        return false;
    }

    private String checkFirst(IASTPreprocessorStatement first) {
        if (first instanceof IASTPreprocessorIfndefStatement) {
            IASTPreprocessorIfndefStatement ifndefStatement = (IASTPreprocessorIfndefStatement) first;
            String condition = String.valueOf(ifndefStatement.getCondition());
            return condition;
        }
        return null;
    }

    private void reportIncludeGuardMissing(IASTNode node, String fileName) {
        reportProblem(ProblemId.INCLUDE_GUARD_MISSING, node, new CtyleCheckerInfo(fileName));
    }

}
