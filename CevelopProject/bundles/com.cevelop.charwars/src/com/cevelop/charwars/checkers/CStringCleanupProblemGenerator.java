package com.cevelop.charwars.checkers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.core.resources.IFile;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.constants.ProblemId;
import com.cevelop.charwars.info.CharwarsInfo;
import com.cevelop.charwars.quickfixes.cstring.cleanup.CStringCleanupQuickFix;
import com.cevelop.charwars.utils.analyzers.BEAnalyzer;
import com.cevelop.charwars.utils.analyzers.FunctionAnalyzer;


public class CStringCleanupProblemGenerator {

    public static List<ProblemReport> generate(IFile file, IASTExpression expression) {
        List<ProblemReport> problemReports = new ArrayList<>();
        for (Function function : CStringCleanupQuickFix.functionMap.keySet()) {
            if (FunctionAnalyzer.isCallToFunction(expression, function)) {
                IASTNode parent = expression.getParent();
                while (parent instanceof IASTCastExpression) {
                    parent = parent.getParent();
                }

                if (BEAnalyzer.isAssignment(parent) || parent instanceof IASTEqualsInitializer) {
                    IASTFunctionCallExpression functionCall = (IASTFunctionCallExpression) expression;
                    IASTInitializerClause[] args = functionCall.getArguments();
                    if (args.length > 0 && ASTAnalyzer.isConversionToCharPointer(args[0])) {
                        ProblemReport report = ProblemReport.create(file, ProblemId.C_STRING_CLEANUP_PROBLEM, functionCall, new CharwarsInfo().also(
                                i -> i.nodeName = function.getName()));
                        if (report != null) {
                            problemReports.add(report);
                        }
                        break;
                    }
                }
            }
        }
        return problemReports;
    }
}
